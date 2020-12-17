package com.onceblabs.onceble

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