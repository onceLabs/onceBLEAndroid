package com.onceblabs.onceble

data class OBGattRequest(
    var requestType: OBGATTRequestType,
    var operation: (() -> Unit)
)