package com.oncelabs.oncebleandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

import com.oncelabs.onceble.core.central.OBCentralManager
import com.oncelabs.onceble.core.central.OBEvent
import com.oncelabs.onceble.core.peripheral.ConnectionState
import com.teknikio.tekniverse.model.Bluebird

class MainActivity : AppCompatActivity() {

    private var obCentralManager: OBCentralManager? = null
    private var bluebird: Bluebird? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        obCentralManager = OBCentralManager(loggingEnabled = false, context = this)

        val bbInstance = Bluebird(null,this)

        obCentralManager?.register(bbInstance)

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
////            println("Device discovered")
//            println("${_obAdvertisementData.name} ${_obAdvertisementData.address}")
//
//            _obPeripheral.rssiHistorical.observe(this as LifecycleOwner, Observer {
//                println("RSSI = ${_obPeripheral.latestAdvData.value?.name} ${it.first()}")
//            })
        })

        obCentralManager?.on(OBEvent.DiscoveredRegisteredType { _obPeripheral, _obAdvertisementData  ->
            Log.d("BLEManager", "Found Registered Type")
            bluebird = (_obPeripheral as Bluebird)
            bluebird?.connect {
                when (it) {
                    ConnectionState.connecting -> Log.d("BLEManager", "Connecting Bluebird")
                    ConnectionState.connected -> Log.d("BLEManager", "Connected Bluebird")
                    ConnectionState.connectionFailed -> Log.d("BLEManager", "Connection Failed to Bluebird")
                    ConnectionState.performingGattDiscovery -> Log.d("BLEManager", "Performing Gatt Discovery")
                    ConnectionState.disconnected -> Log.d("BLEManager", "Disconnected Bluebird")
                    ConnectionState.gattDiscoveryFailed -> Log.d("BLEManager", "Gatt discovery failed for Bluebird")
                    ConnectionState.completedGattDiscovery -> {
                        Log.d("BLEManager", "Gatt Discovery Completed Bluebird")// Ready to go at this point
                        (_obPeripheral as Bluebird).setLEDColor(0)
                    }
                }
            }
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