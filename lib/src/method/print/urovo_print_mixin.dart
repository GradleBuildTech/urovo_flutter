import 'package:flutter/services.dart';
import 'package:urovo_flutter/src/utils/channel_tag.dart';

mixin UrovoPrintMixin {
  static const platform = MethodChannel(ChannelTag.channel);
  Future<void> onPrint() async {
    await platform.invokeMethod(ChannelTag.methodPrint);
  }
}
