package com.oncelabs.onceble

data class OBGattRequest(
    var requestType: OBGATTRequestType,
    var operation: (() -> Unit)
)