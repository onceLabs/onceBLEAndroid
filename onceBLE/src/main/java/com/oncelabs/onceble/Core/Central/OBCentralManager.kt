package com.oncelabs.onceble.Core.Central

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import com.oncelabs.onceble.Core.Peripheral.OBPeripheral
import java.util.*


enum class ScanState{
    Idle,
    Scanning,
    Unknown
}

typealias OBPeripheralDiscoveredHandler = (OBPeripheral) -> Unit
typealias BluetoothAdapterStateChangedHandler = (Int) -> Unit

class OBCentralManager(loggingEnabled: Boolean, mockMode: Boolean = false, context: Context) {

    //Handlers
    private var handlers = mutableMapOf<Int,Any>()

    // Deprecate below in favor of new event style above
    private var obperipheralDiscoveryHandler: OBPeripheralDiscoveredHandler? = null
    private var bluetoothAdapterStateChangedHandler: BluetoothAdapterStateChangedHandler? = null

    //Private
    private val context           = context
    private val REQUEST_ENABLE_BT          = 1
    private val REQUEST_COARSE_LOCATION    = 2
    private var scanState                  = ScanState.Idle

    private val leDeviceMap: MutableMap<String, OBPeripheral>  = mutableMapOf()
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner: BluetoothLeScanner? = null

    init {

        bluetoothAdapter.bluetoothLeScanner?.let{
            bluetoothLeScanner = it
        }

        setupBluetoothAdapterStateHandler()

        if(bluetoothAdapter.state == BluetoothAdapter.STATE_OFF){
            println("Enabling bluetooth adapter")
            bluetoothAdapter.enable()
        }
        else {
            println("Bluetooth adapter is already enabled")
        }

        this.on(OBEvent.ConnectedPeripheral{
            print("Connected peripheral $it")
        })

        this.on(OBEvent.BleReady{
            print("BLE Ready")
        })
    }

    public  fun on(event: OBEvent){
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

    private fun setupBluetoothAdapterStateHandler(){
        println("Setting up BluetoothAdapterStateHandler")
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
                            println("BluetoothAdapterState: STATE_OFF")
                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            println("BluetoothAdapterState: STATE_TURNING_OFF")
                        }
                        BluetoothAdapter.STATE_ON -> {
                            println("BluetoothAdapterState: STATE_ON")
                            //Call BLE ready handler
                            (handlers.get(OBEvent.raw(OBEvent.BleReady())))?.let {
                                //Not sure how to make sure the handler is the correct type here
                                (it as (() -> Unit))()
                            }
                        }
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            println("BluetoothAdapterState: STATE_TURNING_ON")
                        }
                    }
                    bluetoothAdapterStateChangedHandler?.invoke(state)
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        (context as Activity).registerReceiver(bluetoothAdapterStateReceiver, filter)
    }

    fun onBluetoothAdapterStateChanged(handler: BluetoothAdapterStateChangedHandler){
        this.bluetoothAdapterStateChangedHandler = handler
    }

    fun bleIsEnabled(): Boolean{
        return (bluetoothAdapter.state == BluetoothAdapter.STATE_ON)
    }

    fun getBLEState(): Int{
        return bluetoothAdapter.state
    }

    public fun onOBPeripheralDiscovered(handler: OBPeripheralDiscoveredHandler){
        this.obperipheralDiscoveryHandler = handler
    }

    // Start scan
    public fun startScanning(options: OBScanOptions? = null){
        println("Starting scanning")
        // Make sure we aren't already scanning
        if (scanState == ScanState.Idle || scanState == ScanState.Unknown) {

            scanState = ScanState.Idle

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

            options?.let {

                // if options defined

            } ?: run {

                println("Scan started")
                //bluetoothLeScanner?.startScan(leScanCallback)
                bluetoothLeScanner?.startScan(
                    scanFilters,
                    scanSettings,
                    leScanCallback)
            }
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

            device.address.let { it ->

                // Do we already have this result
                if (!leDeviceMap.containsKey(it)) {
                    println("OBCentralManager: New scan result $result")

                    leDeviceMap[it] = OBPeripheral(
                            device,
                            result,
                            context
                    )
                    leDeviceMap[it]?.let {
                            obPeripheralInstance -> obperipheralDiscoveryHandler?.invoke(obPeripheralInstance)
                    }
                }
                else { // We already have so update with new data
                    //Create new OBAdvertisementData
                    leDeviceMap[it]?.setLatestAdvData(result)
                }
            }
        }
    }
}