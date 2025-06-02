enum EmvOption {
  start(0),
  startWithForceOnline(1);

  final int value;

  const EmvOption(this.value);

  /// Converts the integer value to an [EmvOption].
  static EmvOption fromValue(int value) {
    return EmvOption.values.firstWhere(
      (option) => option.value == value,
      orElse: () => EmvOption.start,
    );
  }
}

/// EmvModel.kt
/// This file defines the data model for EMV (Europay, MasterCard, and Visa) transactions in the Urovo Flutter application.
/// It includes properties for currency code, EMV options, card timeout, and flags for beeper and tap/swipe collision handling.
///
/// @param [currencyCode] Optional currency code for the transaction.
/// @param [emvOption] Optional EMV option indicating the transaction mode (e.g., start or start with force online).
/// @param [cardTimeOut] Timeout duration for card operations in seconds.
/// @param [enableBeeper] Flag to enable or disable the beeper during EMV processing.
/// @param [enableTapSwipeCollision] Flag to enable or disable tap and swipe collision handling.
/// @param [isRefund] Flag to indicate if the transaction is a refund.
class EmvRequest {
  final String currencyCode;
  final EmvOption emvOption;
  final int cardTimeOut;
  final bool enableBeeper;
  final bool enableTapSwipeCollision;
  final bool isRefund;

  EmvRequest({
    this.currencyCode = "682",
    this.emvOption = EmvOption.start,
    this.cardTimeOut = 30,
    this.enableBeeper = true,
    this.enableTapSwipeCollision = false,
    this.isRefund = false,
  });

  /// Converts the request to a map for serialization.
  Map<String, dynamic> toJson() {
    return {
      'currencyCode': currencyCode,
      'emvOption': emvOption.value,
      'cardTimeOut': cardTimeOut,
      'enableBeeper': enableBeeper,
      'enableTapSwipeCollision': enableTapSwipeCollision,
      'isRefund': isRefund,
    };
  }
}
