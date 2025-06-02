import 'package:flutter/services.dart';

import '../../../urovo.dart';

class EmvService {
  /// Singleton instance of [EmvService]
  static final EmvService _instance = EmvService._internal();
  factory EmvService() {
    return _instance;
  }
  EmvService._internal();

  static const _channel = EventChannel(ChannelTag.emv);

  void startListening({
    required EmvRequest emvRequest,
    // required Function(EmvInformation) onEmvEvent,
    Function(Object)? onError,
  }) {
    _channel.receiveBroadcastStream(emvRequest.toJson()).listen(
      (event) {
        print(event);
      },
      onError: (error) {
        onError?.call(error);
      },
      cancelOnError: true,
    );
  }
}
