package com.onceblabs.onceble

enum class OBGATTRequestType {
    write,
    read,
    enableNotification,
    enableIndication,
    disableNotification,
    disableIndication,
    mtuUpdate
}