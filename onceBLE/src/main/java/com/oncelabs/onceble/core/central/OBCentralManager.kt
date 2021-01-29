package com.oncelabs.onceble.core.central

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import com.oncelabs.onceble.core.peripheral.gattClient.OBGattServer

import com.oncelabs.onceble.core.peripheral.OBAdvertisementData
import com.oncelabs.onceble.core.peripheral.OBPeripheral
import com.oncelabs.onceble.OBLog
import com.oncelabs.onceble.core.peripheral.gattClient.OBGatt
import java.util.*


enum class ScanState{
    Idle,
    Scanning,
    Unknown
}

class OBCentralManager(loggingEnabled: Boolean, mockMode: Boolean = false, context: Context) {

    // Handlers
    private var handlers = mutableMapOf<Int,Any>()

    // Private
    private var registeredPeripheralTypes: MutableList<OBGattServer<out OBGatt>> = mutableListOf()
    private val context           = context
    private val REQUEST_ENABLE_BT          = 1
    private val REQUEST_COARSE_LOCATION    = 2
    private var scanState                  = ScanState.Idle

    //
    private val leDeviceMap: MutableMap<String, OBPeripheral<out OBGatt>>  = mutableMapOf()
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner: BluetoothLeScanner? = null

    private val obLog = OBLog(loggingEnabled)

    init {

        bluetoothAdapter.bluetoothLeScanner?.let{
            bluetoothLeScanner = it
        }

        setupBluetoothAdapterStateHandler()

        if(bluetoothAdapter.state == BluetoothAdapter.STATE_OFF){
            obLog.log("Enabling bluetooth adapter")
            bluetoothAdapter.enable()
        }
        else {
            obLog.log("Bluetooth adapter is already enabled")
        }

    }

    fun on(event: OBEvent){
        when (event){
            is OBEvent.ConnectedPeripheral -> {
                event.handler?.let { handlers.put(OBEvent.raw(event), it) }
            }
            is OBEvent.FailedToConnectPeripheral -> {
                event.handler?.let { handlers.put(OBEvent.raw(event), it) }
            }
            is OBEvent.DisconnectedPeripheral -> {
                event.handler?.let { handlers.put(OBEvent.raw(event), it) }
            }
            is OBEvent.DiscoveredPeripheral -> {
                event.handler?.let { handlers.put(OBEvent.raw(event), it) }
            }
            is OBEvent.BleReady -> {
                event.handler?.let { handlers.put(OBEvent.raw(event), it) }
            }
            is OBEvent.DiscoveredRegisteredType -> {
                event.handler?.let { handlers.put(OBEvent.raw(event), it) }
            }
        }
    }

    fun <G : OBGatt> register(customPeripheralType: OBGattServer<G>){
        this.registeredPeripheralTypes.add(customPeripheralType)
    }

    private fun setupBluetoothAdapterStateHandler(){
        obLog.log("Setting up BluetoothAdapterStateHandler")
        val bluetoothAdapterStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> {
                            obLog.log("BluetoothAdapterState: STATE_OFF")
                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            obLog.log("BluetoothAdapterState: STATE_TURNING_OFF")
                        }
                        BluetoothAdapter.STATE_ON -> {
                            obLog.log("BluetoothAdapterState: STATE_ON")
                            //Call BLE ready handler
                            (handlers[OBEvent.raw(OBEvent.BleReady())] as (() -> Unit))()
                        }
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            obLog.log("BluetoothAdapterState: STATE_TURNING_ON")
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        (context as Activity).registerReceiver(bluetoothAdapterStateReceiver, filter)
    }

    fun bleIsEnabled(): Boolean{
        return (bluetoothAdapter.state == BluetoothAdapter.STATE_ON)
    }

    // Start scan
    fun startScanning(options: OBScanOptions? = null){
        obLog.log("Starting scanning")
        // Make sure we aren't already scanning
        if (scanState == ScanState.Idle || scanState == ScanState.Unknown) {

            scanState = ScanState.Scanning

            val testUuid = ParcelUuid(UUID.fromString("0000cbbb-0000-1000-8000-00805f9b34fb"))

            // Not used for now
            val scanFilter =
                ScanFilter
                    .Builder().build()
                    //.setServiceUuid(testUuid).build()


            // Not used for now
            val scanSettings: ScanSettings =
                ScanSettings
                    .Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

            val scanFilters: MutableList<ScanFilter> = mutableListOf()
            scanFilters.add(scanFilter)

            obLog.log("Scan started")
            bluetoothLeScanner!!.startScan(
                scanFilters,
                scanSettings,
                leScanCallback
            )
        }
    }

    // Stop scan
    public  fun stopScanning(){
        if (scanState == ScanState.Scanning){
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    // Scan callback
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            val device = result.device
            val obAdvertisementData = OBAdvertisementData(result)

            device.address.let { it ->

                // Do we already have this result
                if (!leDeviceMap.containsKey(it)) {
                    obLog.log("OBCentralManager: New scan result $result")
                    var peripheral: OBPeripheral<out OBGatt>? = null
                    for (type in registeredPeripheralTypes) {
                        if (type.isTypeMatchFor(obAdvertisementData, result)!!){
                            peripheral = type.newInstance(obAdvertisementData, result)
                        }
                    }

                    peripheral?.let { p ->

                        leDeviceMap[it] = p

                        leDeviceMap[it]?.let { _obPeripheralInstance ->
                            (handlers[OBEvent.raw(OBEvent.DiscoveredRegisteredType())] as ((Any, OBAdvertisementData) -> Unit))
                                .invoke(_obPeripheralInstance, obAdvertisementData)
                        }

                    } ?: run {
                        leDeviceMap[it] = OBPeripheral(
                            device,
                            obAdvertisementData,
                            context)

                        leDeviceMap[it]?.let { _obPeripheralInstance ->
                            (handlers[OBEvent.raw(OBEvent.DiscoveredPeripheral())] as ((OBPeripheral<out OBGatt>, OBAdvertisementData) -> Unit))
                                .invoke(_obPeripheralInstance, obAdvertisementData)
                        }
                    }


                }
                else { // We already have so update with new data
                    //Create new OBAdvertisementData
                    leDeviceMap[it]?.setLatestAdvData(obAdvertisementData)
                }
            }
        }
    }
}