package com.oncelabs.onceble.Core.Peripheral

data class OBGattRequest(
        var requestType: OBGATTRequestType,
        var operation: (() -> Unit)
)