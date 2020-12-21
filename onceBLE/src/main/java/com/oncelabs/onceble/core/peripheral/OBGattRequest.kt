package com.oncelabs.onceble.core.peripheral

data class OBGattRequest(
        var requestType: OBGATTRequestType,
        var operation: (() -> Unit)
)