package com.oncelabs.onceble.Core.Peripheral

enum class ConnectionState {
    connecting,
    connected,
    performingGattDiscovery,
    completedGattDiscovery,
    gattDiscoveryFailed,
    connectionFailed,
    disconnecting,
    disconnected
}