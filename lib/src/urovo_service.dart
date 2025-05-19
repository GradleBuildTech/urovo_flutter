import 'package:urovo_flutter/src/method/print/urovo_print_mixin.dart';
import 'package:urovo_flutter/src/method/scanner/object/scanner_object.dart';
import 'package:urovo_flutter/src/method/scanner/urovo_scanner_service.dart';

import 'method/beeper/beeper_object.dart';
import 'method/beeper/urovo_beeper_mixin.dart';

///Singleton
class UrovoService with UrovoPrintMixin, UrovoBeeperMixin {
  static final UrovoService _instance = UrovoService._internal();

  factory UrovoService() {
    return _instance;
  }

  UrovoService._internal();

  UrovoScannerService get scannerService =>
      UrovoScannerService(); // Access the scanner service

  // Add your service methods and properties here
  void doMethodAction<T>(dynamic methodObject) {
    switch (T) {
      case UrovoScannerService:
        scannerService.startScan(
          scannerObject:
              (methodObject is ScannerObject) ? methodObject : ScannerObject(),
        );
        break;
      case UrovoPrintMixin:
        onPrint();
        break;
      case UrovoBeeperMixin:
        onBeeper(
            beeperObject: (methodObject is BeeperObject) ? methodObject : null);
        break;
      default:
        throw Exception('Unknown service type: $T');
    }
  }
}
