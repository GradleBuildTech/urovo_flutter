import 'package:flutter/services.dart';
import 'package:urovo_flutter/src/method/print/print_mixin.dart';

import '../urovo.dart';
import 'method/beeper/beeper_mixin.dart';

///Singleton
class UrovoService with PrintMixin, BeeperMixin {
  static final UrovoService _instance = UrovoService._internal();

  factory UrovoService({UrovoDevice? device}) {
    // Initialize the service if it hasn't been initialized yet
    if (!_instance.isInitialized) {
      _instance.initService(device ?? UrovoDevice.urovo);
      _instance.isInitialized = true; // Set the initialized flag
    }
    return _instance;
  }

  bool isInitialized =
      false; // Flag to check if the service has been initialized

  UrovoService._internal();

  static const _channel =
      MethodChannel(ChannelTag.channel); // Define the channel

  //Do action when init service in first time
  void initService(UrovoDevice? device) async {
    await _channel.invokeMethod(ChannelTag.getDevice, device?.value);
  }

  ScannerService get scannerService =>
      ScannerService(); // Access the scanner service

  // Add your service methods and properties here
  void doMethodAction<T>(dynamic methodObject) async {
    switch (T) {
      case ScannerService:
        scannerService.startScan(
          scannerObject:
              (methodObject is ScannerObject) ? methodObject : ScannerObject(),
        );
        break;
      case PrintMixin:
        await onPrint(
          printModel: methodObject is PrintModel ? methodObject : PrintModel(),
        ); // Assuming PrintModel is defined elsewhere
        break;
      case BeeperMixin:
        await onBeeper(
            beeperObject: (methodObject is BeeperObject) ? methodObject : null);
        break;
      default:
        throw Exception('Unknown service type: $T');
    }
  }
}
