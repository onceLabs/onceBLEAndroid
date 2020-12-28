package com.oncelabs.onceble.core.peripheral

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