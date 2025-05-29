import 'package:flutter/services.dart';
import 'package:urovo_flutter/src/method/print/print_object.dart';
import 'package:urovo_flutter/src/utils/channel_tag.dart';

mixin PrintMixin {
  static const platform = MethodChannel(ChannelTag.channel);
  Future<void> onPrint({required PrintModel printModel}) async {
    await platform.invokeMethod(ChannelTag.methodPrint, printModel.toJson);
  }
}
