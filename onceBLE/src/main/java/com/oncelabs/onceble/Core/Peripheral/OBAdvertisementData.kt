package com.oncelabs.onceble.Core.Peripheral

import android.bluetooth.le.ScanResult
import android.util.SparseArray

data class OBAdvertisementData(val scanResult: ScanResult) {
    var systemInstance = scanResult
    var address = scanResult.device.address
    var primaryPhy = scanResult.primaryPhy
    var secondaryPhy = scanResult.secondaryPhy
    var advInterval = scanResult.periodicAdvertisingInterval
    var connectable = scanResult.isConnectable
    var manufacturerData = scanResult.scanRecord?.manufacturerSpecificData
    var timeStamp = scanResult.timestampNanos
    var name = scanResult.scanRecord?.deviceName
    var flags = scanResult.scanRecord?.advertiseFlags//this can be further broken down
    var rawPacket = scanResult.scanRecord?.bytes
    var serviceUuids = scanResult.scanRecord?.serviceUuids
    var serviceData = scanResult.scanRecord?.serviceData
    var solicitedServiceUuids = scanResult.scanRecord?.serviceSolicitationUuids
    var transmitPowerObserved = scanResult.txPower//Observed by phone
    var txPowerClaimed = scanResult.scanRecord?.txPowerLevel//What is stated in the Adv packet
}