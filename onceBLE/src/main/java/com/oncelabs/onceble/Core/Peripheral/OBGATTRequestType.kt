package com.oncelabs.onceble.Core.Peripheral

enum class OBGATTRequestType {
    write,
    read,
    enableNotification,
    enableIndication,
    disableNotification,
    disableIndication,
    mtuUpdate
}