package com.oncelabs.onceble.Core.Peripheral.GATTClient

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import java.util.*

class OBGatt {

    var services: MutableMap<UUID, OBService> = mutableMapOf()

    fun addServices(services: Array<OBService>){
        services.forEach{
            this.services[it.uuid] = it
        }
    }

    fun discovered(services: MutableList<BluetoothGattService>, gatt: BluetoothGatt){
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
                            predefinedCharacteristic.onFound(
                                OBCharacteristic(
                                    foundCharacteristic,
                                    gatt
                                )
                            )
                        }
                    } ?: run {

                    }
                }
            }
        }
    }

    fun discovered(descriptors: Array<OBDescriptor>, characteristic: Array<BluetoothGattCharacteristic>){
        //to do
    }

}