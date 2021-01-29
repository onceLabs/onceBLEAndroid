package com.oncelabs.onceble.core.central

import com.oncelabs.onceble.core.peripheral.OBAdvertisementData
import com.oncelabs.onceble.core.peripheral.OBPeripheral
import com.oncelabs.onceble.core.peripheral.gattClient.OBGatt

//typealias handler = (OBPeripheral) -> Unit
//enum class _OBEvent {
//    ConnectedPeripheral(handler),
//    FailedToConnectPeripheral,
//    DisconnectedPeripheral,
//    DiscoveredPeripheral,
//    BleReady,
//    DiscoveredRegisteredType
//}

typealias OBPeripheralDiscoveredHandler = (OBPeripheral<out OBGatt>) -> Unit
typealias BluetoothAdapterStateChangedHandler = (Int) -> Unit

sealed class OBEvent{
    class ConnectedPeripheral(val handler : ((OBPeripheral<out OBGatt>) -> Unit)? = null) : OBEvent()
    class FailedToConnectPeripheral(val handler : ((OBPeripheral<out OBGatt>) -> Unit)? = null) : OBEvent()
    class DisconnectedPeripheral(val handler : ((OBPeripheral<out OBGatt>) -> Unit)? = null) : OBEvent()
    class DiscoveredPeripheral(val handler : ((OBPeripheral<out OBGatt>, OBAdvertisementData) -> Unit)? = null) : OBEvent()
    class BleReady(val handler : (() -> Unit)? = null) : OBEvent()
    class DiscoveredRegisteredType(val handler : ((Any, OBAdvertisementData) -> Unit)? = null) : OBEvent()

    companion object{
        fun raw(event: OBEvent): Int{
            return when (event) {
                is ConnectedPeripheral -> 0
                is FailedToConnectPeripheral -> 1
                is DisconnectedPeripheral -> 2
                is DiscoveredPeripheral -> 3
                is BleReady -> 4
                is DiscoveredRegisteredType -> 5
            }
        }
    }
}