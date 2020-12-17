package com.oncelabs.onceble

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