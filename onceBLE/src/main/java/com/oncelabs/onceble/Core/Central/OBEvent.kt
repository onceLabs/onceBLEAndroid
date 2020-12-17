package com.oncelabs.onceble.Core.Central

import com.oncelabs.onceble.Core.Peripheral.OBAdvertisementData
import com.oncelabs.onceble.Core.Peripheral.OBPeripheral
//typealias handler = (OBPeripheral) -> Unit
//enum class _OBEvent {
//    ConnectedPeripheral(handler),
//    FailedToConnectPeripheral,
//    DisconnectedPeripheral,
//    DiscoveredPeripheral,
//    BleReady,
//    DiscoveredRegisteredType
//}

sealed class OBEvent{
    class ConnectedPeripheral(val handler : ((OBPeripheral) -> Unit)? = null) : OBEvent()
    class FailedToConnectPeripheral(val handler : ((OBPeripheral) -> Unit)? = null) : OBEvent()
    class DisconnectedPeripheral(val handler : ((OBPeripheral) -> Unit)? = null) : OBEvent()
    class DiscoveredPeripheral(val handler : ((OBPeripheral, OBAdvertisementData) -> Unit)? = null) : OBEvent()
    class BleReady(val handler : (() -> Unit)? = null) : OBEvent()
    class DiscoveredRegisteredType(val handler : ((Any, OBPeripheral) -> Unit)? = null) : OBEvent()

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