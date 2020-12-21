package com.oncelabs.onceble.core.peripheral

import android.bluetooth.le.ScanResult
import android.os.Build

data class OBAdvertisementData(val scanResult: ScanResult) {
    var systemInstance = scanResult
    var address = scanResult.device.address
    var primaryPhy = if (Build.VERSION.SDK_INT >= 26) scanResult.primaryPhy else null
    var secondaryPhy = if (Build.VERSION.SDK_INT >= 26) scanResult.secondaryPhy else null
    var advInterval = if (Build.VERSION.SDK_INT >= 26) scanResult.periodicAdvertisingInterval else null
    var connectable = if (Build.VERSION.SDK_INT >= 26) scanResult.isConnectable else null
    var manufacturerData = scanResult.scanRecord?.manufacturerSpecificData
    var timeStamp = scanResult.timestampNanos
    var name = scanResult.scanRecord?.deviceName
    var flags = scanResult.scanRecord?.advertiseFlags//this can be further broken down
    var rawPacket = scanResult.scanRecord?.bytes
    var serviceUuids = scanResult.scanRecord?.serviceUuids
    var serviceData = scanResult.scanRecord?.serviceData
//    var solicitedServiceUuids = scanResult.scanRecord?.serviceSolicitationUuids
    var transmitPowerObserved = if (Build.VERSION.SDK_INT >= 26) scanResult.txPower else null //Observed by phone
    var txPowerClaimed = scanResult.scanRecord?.txPowerLevel //What is stated in the Adv packet
}