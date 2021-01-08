package com.oncelabs.onceble.core.peripheral.gattClient

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.MutableLiveData
import java.lang.ref.WeakReference
import java.util.*

class OBCharacteristic {

    //
    var value = MutableLiveData<ByteArray>()

    //
    var uuid: UUID
    var descriptors: Array<OBDescriptor>
    var onFound: GattCompletionHandler<OBCharacteristic>

    //
    private var writeCompletionHandler  : ((success: Boolean) -> Unit)? = null
    private var readCompletionHandler   : ((ByteArray?) -> Unit)?       = null
    private var systemCharacteristic    : BluetoothGattCharacteristic?  = null
    private var gatt                    : WeakReference<OBGatt>?        = null

    constructor(characteristicUUID: UUID, onFound: GattCompletionHandler<OBCharacteristic>, descriptors: Array<OBDescriptor>){
        this.uuid = characteristicUUID
        this.onFound = onFound
        this.descriptors = descriptors
    }

    constructor(characteristic: BluetoothGattCharacteristic, gatt: OBGatt): this(characteristic.uuid, {}, arrayOf()){
        this.gatt = WeakReference(gatt)
        this.systemCharacteristic = characteristic
    }

    fun setNotificationState( enabled: Boolean){
        this.gatt?.let {
            systemCharacteristic?.let { char ->
                it.get()?.setCharacteristicNotification(char, enabled)
            }
        }
    }

    fun setIndicationState( enabled: Boolean){
        this.gatt?.let {
            systemCharacteristic?.let { char ->
                it.get()?.setCharacteristicIndication(char, enabled)
            }
        }
    }

    fun read( onRead: ((ByteArray?) -> Unit)){
        readCompletionHandler = onRead
        this.gatt?.let {
            systemCharacteristic?.let { char ->
                it.get()?.read(char)
            }
        }
    }

    fun write( data: ByteArray, withResponse: Boolean, onWrite: ((success: Boolean) -> Unit)){
        writeCompletionHandler = onWrite
        this.gatt?.let {
            systemCharacteristic?.let { char ->
                it.get()?.write(char, data)
            }
        }
    }

    fun valueWritten(success: Boolean, error: Any){
        writeCompletionHandler?.invoke(success)
        writeCompletionHandler = null
    }

    fun valueRead(success: Boolean, error: Any){
        systemCharacteristic?.let { char ->
            readCompletionHandler?.invoke(char.value)
        }
    }
}
