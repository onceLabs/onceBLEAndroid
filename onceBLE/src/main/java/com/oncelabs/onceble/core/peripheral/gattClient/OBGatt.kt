package com.oncelabs.onceble.core.peripheral.gattClient

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import com.oncelabs.onceble.core.peripheral.OBPeripheral
import java.util.*

open class OBGatt() {
    private var servicesAdded = false

    private var owner: OBPeripheral<out OBGatt>? = null
    var services: MutableMap<UUID, OBService> = mutableMapOf()
    var characteristics: MutableMap<UUID, OBCharacteristic> = mutableMapOf()

    fun addServices(services: Array<OBService>){
        servicesAdded = true
        services.forEach{
            this.services[it.uuid] = it
//            it.characteristics.forEach{characteristic ->
//                this.characteristics[characteristic.uuid] = characteristic
//            }
        }
    }

    fun write(characteristic: BluetoothGattCharacteristic, data: ByteArray, withResponse: Boolean){
        if (withResponse) {
            owner?.write(characteristic, data)
        }
        else {
            owner?.writeNoResponse(characteristic, data)
        }
    }

    fun read(characteristic: BluetoothGattCharacteristic){
        owner?.read(characteristic)
    }

    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic, enable: Boolean) {
        owner?.setCharacteristicNotification(characteristic, enable)
    }

    fun setCharacteristicIndication(characteristic: BluetoothGattCharacteristic, enable: Boolean){
        owner?.setCharacteristicIndication(characteristic, enable)
    }

    fun wrote(characteristic: BluetoothGattCharacteristic, status: Int){
        characteristics[characteristic.uuid]?.valueWritten(true, status)
    }

    fun didRead(characteristic: BluetoothGattCharacteristic, status: Int){
        characteristics[characteristic.uuid]?.valueRead(true, status)
    }

    fun wrote(descriptor: BluetoothGattDescriptor, status: Int){

    }

    fun updated(characteristic: BluetoothGattCharacteristic){
        characteristics[characteristic.uuid]?.updated()
    }

    fun discovered(services: MutableList<BluetoothGattService>, gatt: BluetoothGatt, owner: OBPeripheral<out OBGatt>){
        this.owner = owner
        if(!servicesAdded){
            services.forEach {
                var tempCharacteristics = mutableListOf<OBCharacteristic>()
                it.characteristics.forEach { char ->
                    tempCharacteristics.add(OBCharacteristic(char, this))
                }

                this.services[it.uuid] = OBService(it.uuid, {}, tempCharacteristics.toTypedArray())
            }
            return
        }

        services.forEach { s ->
            this.services.forEach {
                    if (s.uuid == it.key) {
                    print("OBGatt: assigned service ${s.uuid}")
                    it.value.onFound(OBService(s))
                }
            }
            for (foundCharacteristic in s.characteristics) {
                this.services[s.uuid]?.let { service ->
                    service.characteristics.forEach { predefinedCharacteristic ->
                        if (foundCharacteristic.uuid == predefinedCharacteristic.uuid) {
                            print("OBGatt: assigned characteristic ${predefinedCharacteristic.uuid}")
                            val char = OBCharacteristic(foundCharacteristic, this)
                            this.characteristics[predefinedCharacteristic.uuid] = char
                            predefinedCharacteristic.onFound(char)
                        }
                    }
                } ?: run {

                }
            }
        }
    }

    fun discovered(descriptors: Array<OBDescriptor>, characteristic: Array<BluetoothGattCharacteristic>){
        //to do
    }

}