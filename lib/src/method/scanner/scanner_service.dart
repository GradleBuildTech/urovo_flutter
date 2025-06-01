import 'dart:async';

import 'package:flutter/services.dart';
import 'package:urovo_flutter/src/method/scanner/object/scanner_object.dart';
import 'package:urovo_flutter/src/utils/channel_tag.dart';

///[ScannerService] is a singleton class that provides a service for
/// scanning barcodes using the Urovo device.
/// It uses an EventChannel to listen for scan events and provides
/// methods to start and stop scanning.
/// It also provides a dispose method to clean up resources.

mixin class ScannerService {
  ///Singleton

  static final ScannerService _instance = ScannerService._internal();
  factory ScannerService() {
    return _instance;
  }
  ScannerService._internal();

  static const _channel = EventChannel(ChannelTag.eventMethodScanner);

  StreamSubscription? _subscription;

  /// Starts scanning for barcodes.
  /// [onScanResult] is a callback function that will be called with the
  /// scan result as a string.
  void startScan({
    Function(String)? onScanResult,
    Function(Object)? onError,
    required ScannerObject scannerObject,
  }) {
    _subscription =
        _channel.receiveBroadcastStream(scannerObject.toJson).listen(
      (event) {
        onScanResult?.call(event.toString());
      },
      onError: (error) {
        onError?.call(error);
      },
      onDone: () {
        // Handle stream completion if needed
        // For example, you can stop scanning here if needed
        stopScan();
      },
      cancelOnError: true,
    );
  }

  /// Stops scanning for barcodes.
  /// This method cancels the subscription to the scan events.
  void stopScan() {
    _subscription?.cancel();
    _subscription = null;
  }

  void dispose() {
    stopScan();
  }
}
