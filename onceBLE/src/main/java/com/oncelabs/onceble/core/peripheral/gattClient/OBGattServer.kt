package com.oncelabs.onceble.core.peripheral.gattClient

import android.bluetooth.le.ScanResult
import com.oncelabs.onceble.core.peripheral.OBAdvertisementData
import com.oncelabs.onceble.core.peripheral.OBPeripheral

interface OBGattServer {
    val customGatt: OBGatt?
    fun isTypeMatchFor(advData: OBAdvertisementData, peripheral: ScanResult): Boolean?
    fun newInstance(advData: OBAdvertisementData, peripheral: ScanResult): OBPeripheral?
}