package com.example.urovo_flutter.model

import android.os.Bundle

data class MagCardInformation(
    val pan: String? = null,
    val track1: String? = null,
    val track2: String? = null,
    val track3: String? = null,
    val serviceCode: String? = null,
    val expireDate: String? = null
)

fun Bundle.toMagCardInformation(): MagCardInformation {
    return MagCardInformation(
        pan = getString("PAN"),
        track1 = getString("TRACK1"),
        track2 = getString("TRACK2"),
        track3 = getString("TRACK3"),
        serviceCode = getString("SERVICE_CODE"),
        expireDate = getString("EXPIRED_DATE")
    )
}

fun MagCardInformation.toJson(timeOut: String): Map<String, Any?> {
    return mapOf(
        "pan" to pan,
        "track1" to track1,
        "track2" to track2,
        "track3" to track3,
        "serviceCode" to serviceCode,
        "expireDate" to expireDate,
        "timeOut" to timeOut
    )
}