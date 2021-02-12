package com.oncelabs.onceble.core.peripheral

import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.SparseArray
import androidx.core.util.forEach

data class OBAdvertisementData(val scanResult: ScanResult) {
    var systemInstance = scanResult
    var address = scanResult.device.address
    var primaryPhy = if (Build.VERSION.SDK_INT >= 26) scanResult.primaryPhy else null
    var secondaryPhy = if (Build.VERSION.SDK_INT >= 26) scanResult.secondaryPhy else null
    var advInterval = if (Build.VERSION.SDK_INT >= 26) scanResult.periodicAdvertisingInterval else null
    var connectable = if (Build.VERSION.SDK_INT >= 26) scanResult.isConnectable else null
    var manufacturerData = byteArrayOf()
    var timeStamp = scanResult.timestampNanos
    var name = scanResult.scanRecord?.deviceName
    var flags = scanResult.scanRecord?.advertiseFlags//this can be further broken down
    var rawPacket = scanResult.scanRecord?.bytes
    var serviceUuids = scanResult.scanRecord?.serviceUuids
    var serviceData = scanResult.scanRecord?.serviceData
    //    var solicitedServiceUuids = scanResult.scanRecord?.serviceSolicitationUuids
    var transmitPowerObserved = if (Build.VERSION.SDK_INT >= 26) scanResult.txPower else null //Observed by phone
    var txPowerClaimed = scanResult.scanRecord?.txPowerLevel //What is stated in the Adv packet
    var rssi = scanResult.rssi
    var serviceSolicitationUuids = if (Build.VERSION.SDK_INT >= 29) scanResult.scanRecord?.serviceSolicitationUuids else null

    var searchableString = ""

    init{
        manufacturerData = sparseArrayToByteArray(scanResult.scanRecord?.manufacturerSpecificData)
        searchableString = "${name ?: ""} " +
                "$address " +
                "${byteArrayToString(manufacturerData)} " +
                "${serviceUuids ?: ""} " +
                "${if(connectable == true) "Yes" else "No"} " +
                "${serviceData ?: ""}"
    }

    val description: String get() {
        val noValue = "NO VALUE"
        val connectablePrintValue           = if(connectable != null) "$connectable" else noValue
        val manufacturerDataPrintValue      = if(manufacturerData.isNotEmpty()) byteArrayToString(manufacturerData) else noValue
        val serviceDataPrintValue           = if(!serviceData.isNullOrEmpty()) "$serviceData" else noValue
        val servicesPrintValue              = if(!serviceUuids.isNullOrEmpty()) "$serviceUuids" else noValue
        val solicitedServiceUUIDsPrintValue = if(!serviceSolicitationUuids.isNullOrEmpty()) "$serviceSolicitationUuids" else noValue
        val transmitPowerLevelPrintValue    = if(transmitPowerObserved != null) "$transmitPowerObserved" else noValue
        val localNamePrintValue             = if(name != null) "$name" else noValue
        val rssiPrintValue                  = "$rssi"
        val timeStampPrintValue             = "$timeStamp"
        val advIntervalEstimatePrintValue   = if(advInterval != null) "$advInterval" else noValue

        return """
            Device Name: $localNamePrintValue
            RSSI: $rssiPrintValue
            Connectable: $connectablePrintValue
            Manufacturer Data: $manufacturerDataPrintValue
            Services: $servicesPrintValue
            ServiceData: $serviceDataPrintValue
            Solicited Service UUIDs: $solicitedServiceUUIDsPrintValue
            Transmit Power Level: $transmitPowerLevelPrintValue
            Adv Estimate: $advIntervalEstimatePrintValue
        """
    }


    private fun sparseArrayToByteArray(sparseArray: SparseArray<ByteArray>?): ByteArray{
        var byteArray = byteArrayOf()

        sparseArray?.forEach{key, value ->
            value.forEach {
                byteArray += it
            }
        }

        return byteArray
    }

    private fun byteArrayToString(byteArray: ByteArray): String{
        var string = ""

        byteArray?.forEach {
            val byteString = String.format("%02X", it)
            string = "$string$byteString"
        }

        return if(string.isBlank()){
            ""
        }else {
            "0x$string"
        }
    }
}