import 'package:flutter/services.dart';

import '../../utils/channel_tag.dart';
import 'beeper_object.dart';

mixin UrovoBeeperMixin {
  static const platform = MethodChannel(ChannelTag.channel);

  Future<void> onBeeper({int? ctms, int? msecTime}) async {
    await platform.invokeMethod(ChannelTag.methodBeeper,
        BeeperObject(cnts: ctms, msecTime: msecTime).toJson);
  }
}
