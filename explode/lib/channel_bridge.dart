import 'package:flutter/services.dart';

class ChannelBridge {
  static final ChannelBridge _instance = ChannelBridge._();

  ChannelBridge._();

  final MethodChannel channel = const MethodChannel('nativeBridgeChannel');

  static ChannelBridge get instance => _instance;
}
