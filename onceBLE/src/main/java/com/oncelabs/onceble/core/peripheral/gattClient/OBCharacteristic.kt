package com.oncelabs.onceble.core.peripheral.gattClient

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import kotlin.coroutines.resume

class SettableLiveData<T>( initialValue: T,private val setter: (T) -> Unit) {
    private val data = MutableLiveData<T>()
    fun observable():LiveData<T> = data
    var value: T = initialValue
        set(value) {
            data.postValue(value)
            setter(value)
        }
    init {
        data.postValue(initialValue)
    }
}

class OBCharacteristic {

    var value = SettableLiveData<ByteArray>(byteArrayOf(0)){ this.asyncWrite(it, false, null) }
        get() {
            asyncRead { print(it) }
            return field
        }

    //
    var uuid: UUID
    var descriptors: Array<OBDescriptor>
    var onFound: GattCompletionHandler<OBCharacteristic>
    private var _onChanged: ((ByteArray) -> Unit)? = null

    //
    private var writeCompletionHandler  : ((success: Boolean) -> Unit)? = null
    private var readCompletionHandler   : ((ByteArray?) -> Unit)?       = null
    private var systemCharacteristic    : BluetoothGattCharacteristic?  = null
    private var gatt                    : WeakReference<OBGatt>?        = null

    private var readAwait: Deferred<ByteArray>?   = null

    constructor(characteristicUUID: UUID, onFound: GattCompletionHandler<OBCharacteristic>, descriptors: Array<OBDescriptor>){
        this.uuid = characteristicUUID
        this.onFound = onFound
        this.descriptors = descriptors
    }

    fun onChanged( onChangeHandler: ((ByteArray) -> Unit)){
        this._onChanged = onChangeHandler
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

    suspend fun syncRead(): ByteArray {
        return suspendCancellableCoroutine<ByteArray> { continuation ->
            readCompletionHandler = { bytes ->
                bytes?.let {
                    continuation.run { resume(it) }
                }
            }
            this.gatt?.let {
                systemCharacteristic?.let { char ->
                    it.get()?.read(char)
                }
            }
        }
    }

    fun asyncRead( onRead: ((ByteArray?) -> Unit)) {
        readCompletionHandler = onRead
        this.gatt?.let {
            systemCharacteristic?.let { char ->
                it.get()?.read(char)
            }
        }
    }

    suspend fun syncWrite( data: ByteArray, withResponse: Boolean): Boolean {
        return suspendCancellableCoroutine<Boolean> { continuation ->
            writeCompletionHandler = {
                continuation.resume(it)
            }
            this.gatt?.let {
                systemCharacteristic?.let { char ->
                    it.get()?.write(char, data, withResponse)
                }
            }
        }
    }

    fun asyncWrite( data: ByteArray, withResponse: Boolean, onWrite: ((success: Boolean) -> Unit)?){
        writeCompletionHandler = onWrite
        this.gatt?.let {
            systemCharacteristic?.let { char ->
                it.get()?.write(char, data, withResponse)
            }
        }
    }
    
    fun updated(){
        systemCharacteristic?.value?.let {
            this.value.value = it
            _onChanged?.invoke(it)
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
        readCompletionHandler = null
    }
}
