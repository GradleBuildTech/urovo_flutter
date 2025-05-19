import 'package:flutter/services.dart';
import 'package:urovo_flutter/src/utils/channel_tag.dart';

mixin UrovoScannerMixin {
  static const platform = MethodChannel(ChannelTag.channel);
}
