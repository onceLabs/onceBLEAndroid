package com.oncelabs.onceble.core.peripheral.gattClient

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import java.util.*

class OBCharacteristic {

    var uuid: UUID
    var descriptors: Array<OBDescriptor>
    var onFound: GattCompletionHandler<OBCharacteristic>

    private var valueUpdateHandler: (() -> Unit)? = null
    private var writeCompletionHandler: ((success: Boolean) -> Unit)? = null
    private var systemCharacteristic: BluetoothGattCharacteristic? = null
    private var systemGatt: BluetoothGatt? = null

    constructor(characteristicUUID: UUID, onFound: GattCompletionHandler<OBCharacteristic>, descriptors: Array<OBDescriptor>){
        this.uuid = characteristicUUID
        this.onFound = onFound
        this.descriptors = descriptors
    }

    constructor(characteristic: BluetoothGattCharacteristic, gatt: BluetoothGatt): this(characteristic.uuid, {}, arrayOf()){
        this.systemGatt = gatt
        this.systemCharacteristic = characteristic
    }

    fun onValueChanged(valueUpdateHandler: (() -> Unit)){
        this.valueUpdateHandler = valueUpdateHandler
    }

    fun setNotificationState(enabled: Boolean){

    }

    fun setIndicationState(enabled: Boolean){

    }

    fun read( onRead: ((ByteArray) -> Unit)){

    }

    fun write(data: ByteArray, withResponse: Boolean, onWriteCompleted: ((success: Boolean) -> Unit)){

    }

    fun didUpdateValue(){
        valueUpdateHandler?.invoke()
    }

    fun valueWritten(success: Boolean, error: Any){
        writeCompletionHandler?.invoke(success)
        writeCompletionHandler = null
    }
}
