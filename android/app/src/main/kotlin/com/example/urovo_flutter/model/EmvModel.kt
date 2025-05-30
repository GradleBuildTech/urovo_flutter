package com.example.urovo_flutter.model

import com.urovo.i9000s.api.emv.ContantPara

enum class EmvOption(val value: Int) {
    START(0),
    START_WITH_FORCE_ONLINE(1);

    companion object {
        fun fromValue(value: Int): EmvOption {
            return values().find { it.value == value } ?: START
        }
    }

    fun toOption(): ContantPara.EmvOption {
        return when (this) {
            START -> ContantPara.EmvOption.START
            START_WITH_FORCE_ONLINE -> ContantPara.EmvOption.START_WITH_FORCE_ONLINE
        }
    }
}


/**
 * EmvModel.kt
 * This file defines the data model for EMV (Europay, MasterCard, and Visa) transactions in the Urovo Flutter application.
 * It includes properties for currency code, EMV options, card timeout, and flags for beeper and tap/swipe collision handling.
 *
 * @param currencyCode Optional currency code for the transaction.
 * @param emvOption Optional EMV option indicating the transaction mode (e.g., start or start with force online).
 * @param cardTimeOut Timeout duration for card operations in seconds.
 * @param enableBeeper Flag to enable or disable the beeper during EMV processing.
 * @param enableTapSwipeCollision Flag to enable or disable tap and swipe collision handling.
 * @param isRefund Flag to indicate if the transaction is a refund.
 */
data class EmvModel(
    val currencyCode: String? = null,
    val emvOption: EmvOption? = null, // 0: Start, 1: Start with Force Online
    val cardTimeOut: Int = 30, // Check Card time out in seconds
    val enableBeeper: Boolean = true, // Enable beeper during EMV process
    val enableTapSwipeCollision: Boolean = false, // Enable tap and swipe collision handling
    val isRefund: Boolean =  false
)

/**
 * Converts the EmvModel to a ContantPara object for use with the Urovo SDK.
 */
fun Map<*, *> .toEmvModel(): EmvModel {
    return EmvModel(
        currencyCode = this["currencyCode"] as? String,
        emvOption = (this["emvOption"] as? Int)?.let { EmvOption.fromValue(it) },
        cardTimeOut = (this["cardTimeOut"] as? Int) ?: 30,
        enableBeeper = (this["enableBeeper"] as? Boolean) ?: true,
        enableTapSwipeCollision = (this["enableTapSwipeCollision"] as? Boolean) ?: false
    )
}
