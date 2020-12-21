package com.oncelabs.onceble.Core.Peripheral.GATTClient

import android.bluetooth.le.ScanResult
import com.oncelabs.onceble.Core.Peripheral.OBAdvertisementData
import com.oncelabs.onceble.Core.Peripheral.OBPeripheral

interface OBGattServer {
    val customGatt: OBGatt?
    fun isTypeMatchFor(advData: OBAdvertisementData, peripheral: ScanResult): Boolean?
    fun newInstance(advData: OBAdvertisementData, peripheral: ScanResult): OBPeripheral?
}