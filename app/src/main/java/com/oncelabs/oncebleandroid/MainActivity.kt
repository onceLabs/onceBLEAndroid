package com.oncelabs.oncebleandroid

import android.bluetooth.BluetoothAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oncelabs.onceble.core.central.OBCentralManager
import com.oncelabs.onceble.core.central.OBEvent

class MainActivity : AppCompatActivity() {

    private var obCentralManager: OBCentralManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        obCentralManager = OBCentralManager(loggingEnabled = false, context = this)

        obCentralManager?.bleIsEnabled()?.let{
            if(it){
                startScanning()
            }
            else{
                obCentralManager?.on(OBEvent.BleReady{
                    startScanning()
                })
            }
        }

        obCentralManager?.on(OBEvent.DiscoveredPeripheral{ _obPeripheral, _obAdvertisementData ->
//            println("Device discovered")
            println("${_obAdvertisementData.name} ${_obAdvertisementData.address}")
        })

    }

    private fun startScanning(){
        //val obScanOptions = OBScanOptions(services = arrayOf(ParcelUuid(LOCK_UUID_CONTROL_SERVICE)))
        obCentralManager?.startScanning()
    }

    private fun stopScanning(){
        obCentralManager?.stopScanning()
    }
}