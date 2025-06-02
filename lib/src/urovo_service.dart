import 'package:flutter/services.dart';
import 'package:urovo_flutter/src/method/emv/emv_service.dart';
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

  ///---------------------------------------------------------
  static const _channel =
      MethodChannel(ChannelTag.channel); // Define the channel

  //Do action when init service in first time
  void initService(UrovoDevice? device) async {
    await _channel.invokeMethod(ChannelTag.getDevice, device?.value);
  }

  ///---------------------------------------------------------

  ///[StreamService] is a singleton class that provides access to various services
  ScannerService get scannerService =>
      ScannerService(); // Access the scanner service

  SearchMagCardService get searchMagCardService =>
      SearchMagCardService(); // Access the magnetic card service

  EmvService get emvService => EmvService(); // Access the EMV service
}
