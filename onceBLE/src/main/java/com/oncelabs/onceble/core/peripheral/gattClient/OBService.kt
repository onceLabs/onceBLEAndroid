package com.oncelabs.onceble.core.peripheral.gattClient

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import java.util.*

public typealias GattCompletionHandler<T> = (T) -> Unit

class OBService {

    public var uuid: UUID
    public var onFound: GattCompletionHandler<OBService>
    public var characteristics: Array<OBCharacteristic>

    constructor(serviceUUID: UUID, onFound: GattCompletionHandler<OBService>, characteristics: Array<OBCharacteristic>){
        this.uuid = serviceUUID
        this.onFound = onFound
        this.characteristics = characteristics
    }

    //Used for non-defined gatt
    constructor(service: BluetoothGattService): this(service.uuid, {}, arrayOf())

    //Mock init
}