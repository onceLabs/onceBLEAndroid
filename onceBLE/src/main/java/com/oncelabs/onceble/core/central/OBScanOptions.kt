package com.oncelabs.onceble.core.central

import android.os.ParcelUuid

data class OBScanOptions(

    var allowDuplicates      : Boolean = true,
    var services             : Array<ParcelUuid>,
    var scanDuration         : Int? = null,
    var mininumRSSI          : Int? = null,
    var dataTimeout          : Int? = null,
    var registeredTypesOnly  : Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OBScanOptions

        if (allowDuplicates != other.allowDuplicates) return false
        if (!services.contentEquals(other.services)) return false
        if (scanDuration != other.scanDuration) return false
        if (mininumRSSI != other.mininumRSSI) return false
        if (dataTimeout != other.dataTimeout) return false
        if (registeredTypesOnly != other.registeredTypesOnly) return false

        return true
    }

    override fun hashCode(): Int {
        var result = allowDuplicates.hashCode()
        result = 31 * result + services.contentHashCode()
        result = 31 * result + (scanDuration ?: 0)
        result = 31 * result + (mininumRSSI ?: 0)
        result = 31 * result + (dataTimeout ?: 0)
        result = 31 * result + registeredTypesOnly.hashCode()
        return result
    }
}