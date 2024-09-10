import 'package:explode/channel_bridge.dart';
import 'package:flutter/widgets.dart';

import 'explode.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  final explode = Explode();

  /// Sets up a handler for method calls coming from the native side through the MethodChannel.
  /// Based on the method name received, it triggers the corresponding functionality in the Explode class.
  ChannelBridge.instance.channel.setMethodCallHandler((call) async {
    if (call.method == 'search') {
      explode.search(call.arguments);
    } else if (call.method == 'getAudioUrl') {
      explode.getAudioUrl(call.arguments);
    } else if (call.method == 'dispose') {
      explode.dispose();
    } else if (call.method == 'nextPage') {
      explode.nextPage();
    }
  });
}
