package com.oncelabs.onceble.core.peripheral

enum class OBGATTRequestType {
    write,
    read,
    enableNotification,
    enableIndication,
    disableNotification,
    disableIndication,
    mtuUpdate
}