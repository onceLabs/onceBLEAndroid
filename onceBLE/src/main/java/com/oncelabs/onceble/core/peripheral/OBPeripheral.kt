package com.oncelabs.onceble.core.peripheral


import android.bluetooth.*
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oncelabs.onceble.core.central.OBConnectionOptions
import com.oncelabs.onceble.core.peripheral.gattClient.OBGatt
import com.oncelabs.onceble.core.peripheral.gattClient.OBGattServer
import com.oncelabs.onceble.core.peripheral.gattClient.OBService
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


val CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

typealias ServiceDiscoveryHandler = (MutableList<BluetoothGattService>) -> Unit
typealias CharacteristicValueHandler = (BluetoothGattCharacteristic) -> Unit
typealias ConnectionHandler = (ConnectionState) -> Unit


open class OBPeripheral<G: OBGatt>(device: BluetoothDevice? = null, scanResult: OBAdvertisementData? = null, context: Context): BluetoothGattCallback(), OBGattServer<G>{

    override var obGatt: G? = null
        get(){
            if(field != null) {
                return field
            }

            field = (OBGatt() as G)
            return field
        }


    override fun isTypeMatchFor(advData: OBAdvertisementData, peripheral: ScanResult): Boolean? {
        return  null
    }

    override fun newInstance(advData: OBAdvertisementData, peripheral: ScanResult): OBPeripheral<G>? {
        return null
    }

    // Request queue
    private var gattRequestQueue: Queue<OBGattRequest> = ConcurrentLinkedQueue<OBGattRequest>();
    private var pendingGATTRequest: OBGattRequest? = null

    // Status flags
    var state: LiveData<ConnectionState> = MutableLiveData()
        get() = _state
        private set
    private var _state:MutableLiveData<ConnectionState> = MutableLiveData()

    private var connectionState: ConnectionState = ConnectionState.disconnected
        set(value) {
            _state.postValue(value)
            field = value
        }

    // Handlers
    private var serviceDiscoveryHandler: ServiceDiscoveryHandler? = null
    private var connectionHandler: ConnectionHandler? = null

    var id: String? = device?.address

    private val _latestAdvData = MutableLiveData<OBAdvertisementData>()
    val latestAdvData : LiveData<OBAdvertisementData>
        get() = _latestAdvData

    val rssiHistorical = MutableLiveData<MutableList<Int>>()

    private var systemDevice: BluetoothDevice? = device
    private var gatt: BluetoothGatt? = null

    private var context = context

    init {
        scanResult?.let{
            setLatestAdvData(it)
        }
        _state.value = ConnectionState.disconnected
    }

    @Synchronized
    private fun enqueueOperation(operation: OBGattRequest) {
        gattRequestQueue.add(operation)
        if (pendingGATTRequest == null) {
            doNextOperation()
        }
    }

    @Synchronized
    private fun signalEndOfOperation() {
        print("OBPeripheral: request completed $pendingGATTRequest")
        pendingGATTRequest = null
        if (gattRequestQueue.isNotEmpty()) {
            doNextOperation()
        }
    }

    @Synchronized
    private fun doNextOperation() {

        if (pendingGATTRequest != null) {
            print("OBPeripheral: pending request, abort doNextOperation")
            return
        }

        val gattRequest = gattRequestQueue.poll() ?: run {
            print("OBPeripheral: GATT request queue empty ")
            return
        }
        pendingGATTRequest = gattRequest

        when (gattRequest.requestType) {
            OBGATTRequestType.write -> {
                print("Write")
            }
            OBGATTRequestType.read -> {
                print("Read")
            }
            OBGATTRequestType.enableIndication -> {
                print("Enable Indication")
            }
            OBGATTRequestType.disableIndication -> {
                print("Disable Indication")
            }
            OBGATTRequestType.enableNotification -> {
                print("Enable Notification")
            }
            OBGATTRequestType.disableNotification -> {
                print("Disable Notification")
            }
            OBGATTRequestType.mtuUpdate -> {
                print("MTU Update request")
            }
        }
        gattRequest.operation.invoke()
    }



    fun setLatestAdvData(advData: OBAdvertisementData){
        _latestAdvData.value = advData

        if(rssiHistorical.value == null){
            rssiHistorical.value = mutableListOf()
        }

        rssiHistorical.value?.let{
            val rssiList = it

            if(rssiList.size >= 50){
                rssiList.removeAt(0)
            }
            rssiList.add(advData.scanResult.rssi)
            rssiHistorical.value = rssiList
        }
    }

    fun setPeripheral(device: BluetoothDevice, scanResult: OBAdvertisementData){
        _latestAdvData.value = scanResult
        systemDevice = device
    }

    fun peripheralSet(): Boolean{
        return systemDevice != null
    }

    fun connect(options: OBConnectionOptions? = null, connectionHandler: ConnectionHandler){
        this.connectionHandler = connectionHandler
        val callback = this
        systemDevice?.connectGatt(context, false, callback)
    }

    fun isOkToConnect(): Boolean {
        return (connectionState == ConnectionState.disconnected)
    }

    //private var waitingForDisconnect = false
    fun disconnect(){
        println("Try to disconnect at peripheral")
        gatt?.close()
        gatt?.disconnect()
        /*  Android doesn't disconnect reliably */
        //waitingForDisconnect = true
//        Handler().postDelayed({
//            if (waitingForDisconnect){
//                println("Attempt to disconnect")
//                disconnect()
//            }
//        }, 5000)
    }

    fun onDiscoveredServices(
        handler: ServiceDiscoveryHandler
    ) {
        this.serviceDiscoveryHandler = handler
    }

    override fun onConnectionStateChange(
        gatt: BluetoothGatt?,
        status: Int,
        newState: Int
    ) {
        super.onConnectionStateChange(gatt, status, newState)
        when (newState){
            BluetoothProfile.STATE_DISCONNECTING -> {
                connectionState = ConnectionState.disconnecting
                connectionHandler?.invoke(ConnectionState.disconnecting)
                println("GATT disconnecting")
            }

            BluetoothProfile.STATE_DISCONNECTED -> {
                connectionState = ConnectionState.disconnected
                connectionHandler?.invoke(ConnectionState.disconnected)
                println("GATT disconnected")
            }

            BluetoothProfile.STATE_CONNECTING -> {
                connectionState = ConnectionState.connecting
                connectionHandler?.invoke(ConnectionState.connecting)
                println("GATT connecting")
            }

            BluetoothProfile.STATE_CONNECTED -> {
                connectionState = ConnectionState.connected
                connectionHandler?.invoke(ConnectionState.connected)
                println("GATT connected")
                this.gatt = gatt
                this.gatt?.discoverServices()
                connectionHandler?.invoke(ConnectionState.performingGattDiscovery)
            }
        }
    }

    fun read(
        characteristic: BluetoothGattCharacteristic?
    ) {
        val gattReadRequest= OBGattRequest(OBGATTRequestType.read) {
            characteristic?.let {
                println("Attempt to read characteristic: ${characteristic.uuid}")
                gatt?.readCharacteristic(characteristic)
            }
        }
        enqueueOperation(gattReadRequest)
    }

    fun setCharacteristicIndication(
        characteristic: BluetoothGattCharacteristic,
        enable: Boolean
    ): Boolean? {
        println("Enable notifications")
        val gattIndicationRequest = OBGattRequest(OBGATTRequestType.enableIndication) {
            gatt?.setCharacteristicNotification(characteristic, enable)
            val descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID)

            if (descriptor == null) {
                println("Descriptor is null")
            }

            if (enable) {
                descriptor.value = byteArrayOf(0x02, 0x00)
            } else {
                descriptor.value = byteArrayOf(0x00, 0x00)
            }

            gatt?.writeDescriptor(descriptor)
        }

        enqueueOperation(gattIndicationRequest)
        return true//descriptor write operation successfully started?
    }

    fun write(
        characteristic: BluetoothGattCharacteristic?,
        data: ByteArray?
    ) {
        val gattWriteRequest = OBGattRequest(OBGATTRequestType.write) {
            data?.let {
                for (b in it) {
                    val st = String.format("%02X", b)
                    print(st)
                }
            }
            println("    EndData")

            characteristic?.value = data
            characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            gatt?.writeCharacteristic(characteristic)
        }

        enqueueOperation(gattWriteRequest)
    }

    fun writeNoResponse(
        characteristic: BluetoothGattCharacteristic?,
        data: ByteArray?
    ) {
        val gattWriteRequest = OBGattRequest(OBGATTRequestType.write) {
            data?.let {
                for (b in it) {
                    val st = String.format("%02X", b)
                    print(st)
                }
            }
            println("    EndData")

            characteristic?.value = data
            characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            gatt?.writeCharacteristic(characteristic)
        }

        enqueueOperation(gattWriteRequest)
    }

    fun readRemoteRssi(): Boolean{
        gatt?.let{
            return it.readRemoteRssi()
        }
        return false
    }

    override fun onServicesDiscovered(
        gatt: BluetoothGatt?,
        status: Int
    ) {
        super.onServicesDiscovered(gatt, status)

        gatt?.services?.let {

            this.serviceDiscoveryHandler?.invoke(it)

            obGatt?.discovered(it, gatt, this)

            it.forEach { service ->
                println("OBPeripheral: discovered service with UUID: ${service.uuid}")
                service.characteristics.forEach{ characteristic ->
                    println("OBPeripheral: Discovered characteristic: ${characteristic.uuid}")
                }
            }
        }

        connectionHandler?.invoke(ConnectionState.completedGattDiscovery)
        this.gattDiscoveryCompleted()
    }

    open fun gattDiscoveryCompleted(){
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicRead(gatt, characteristic, status)

        characteristic?.let{
            obGatt?.didRead(it, status)
        }

        pendingGATTRequest?.requestType.let {
            if (it == OBGATTRequestType.read){
                signalEndOfOperation()
            }
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        super.onCharacteristicChanged(gatt, characteristic)

        characteristic?.let{
            obGatt?.updated(it)
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)

        characteristic?.let{
            obGatt?.wrote(it, status)
        }

        pendingGATTRequest?.requestType.let {
            if (it == OBGATTRequestType.write){
                signalEndOfOperation()
            }
        }
    }


    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorWrite(gatt, descriptor, status)

        descriptor?.let {
            obGatt?.wrote(it, status)
        }

        pendingGATTRequest?.requestType.let {
            if (it == OBGATTRequestType.enableIndication || it == OBGATTRequestType.enableNotification){
                signalEndOfOperation()
            }
        }
    }

    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enable: Boolean
    ): Boolean? {
        println("Enable notifications")

        val gattNotificationRequest = OBGattRequest(OBGATTRequestType.enableNotification) {
            gatt?.setCharacteristicNotification(characteristic, enable)
            val descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID)

            //Thread.sleep(10)

            if (enable) {
                descriptor.value = byteArrayOf(0x01, 0x00)
            } else {
                descriptor.value = byteArrayOf(0x00, 0x00)
            }

            gatt?.writeDescriptor(descriptor)
        }

        enqueueOperation(gattNotificationRequest)

        return true//descriptor write operation successfully started?
    }
}