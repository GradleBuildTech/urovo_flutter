import 'package:flutter/services.dart';

import '../../utils/channel_tag.dart';
import 'beeper_object.dart';

mixin UrovoBeeperMixin {
  static const platform = MethodChannel(ChannelTag.channel);

  Future<void> onBeeper({BeeperObject? beeperObject}) async {
    await platform.invokeMethod(ChannelTag.methodBeeper, beeperObject?.toJson);
  }
}
