package com.teknikio.tekniverse.model

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.oncelabs.onceble.core.peripheral.OBAdvertisementData
import com.oncelabs.onceble.core.peripheral.OBPeripheral
import com.oncelabs.onceble.core.peripheral.gattClient.OBGatt
import com.oncelabs.onceble.core.peripheral.gattClient.OBGattServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Bluebird(private var device: BluetoothDevice?, private var context: Context) : OBPeripheral(device = device, context = context) {

    //override var customGatt: OBGatt? = BluebirdGatt(this)
    override var obGatt: OBGatt? = BluebirdGatt()

    override fun isTypeMatchFor(advData: OBAdvertisementData, peripheral: ScanResult): Boolean {
        var isBluebird = false

        Log.d("Bluebird Check", "${advData.name}")

        advData.name.let { name ->
            when (name) {
                "BLUEBIRD" -> {
                    Log.d("Bluebird", "$advData")
                    isBluebird = true
                }

                "BLUEBIRD_OTA" -> {
                    Log.d("Bluebird", "$advData")
                    isBluebird = true
                }

                "TekDFU" -> {
                    Log.d("Bluebird", "$advData")
                    isBluebird = true
                }

                else -> {
                    isBluebird = false
                }
            }
        }

        advData.serviceUuids?.let {
            if (it.contains(ParcelUuid.fromString("00001530-1212-EFDE-1523-785FEABCD123"))) {
                isBluebird = true
            }
        }

        return isBluebird
    }

    override fun gattDiscoveryCompleted() {

        this.getGatt()
        GlobalScope.launch(Dispatchers.Main) {
            getGatt().bluebirdAccelerationCharacteristic?.value?.observable()!!.observeForever {
                Log.d("ExampleDeivce", "Notification")
            }
        }

        getGatt().bluebirdAccelerationCharacteristic?.onChanged{
            Log.d("ExampleDevice", "$it")
        }

        getGatt().bluebirdAccelerationCharacteristic?.let {
            it.setNotificationState(true)
        }
    }
    override fun newInstance(advData: OBAdvertisementData, peripheral: ScanResult): OBPeripheral {
        return Bluebird(peripheral.device ,this.context)
    }

    fun getGatt(): BluebirdGatt {
        return obGatt as BluebirdGatt
    }

    fun setLEDColor(color: Int) {
        val colorBytes = byteArrayOf(0xf, 0xf, 0x0)

        getGatt()?.bluebirdLedColorCharacteristic?.let {
            it.asyncWrite(colorBytes, true) { ok ->
                if (ok) {
                    Log.d("Bluebird", "LED color bytes sent!")
                } else {
                    Log.e("Bluebird", "LED color bytes not sent!!")
                }
            }
        }
    }
}