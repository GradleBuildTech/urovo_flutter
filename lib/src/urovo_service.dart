import 'package:urovo_flutter/src/method/print/urovo_print_mixin.dart';

import 'method/beeper/urovo_beeper_mixin.dart';

///Singleton
class UrovoService with UrovoPrintMixin, UrovoBeeperMixin {
  static final UrovoService _instance = UrovoService._internal();

  factory UrovoService() {
    return _instance;
  }

  UrovoService._internal();

  // Add your service methods and properties here
  void someMethod() {}
}
