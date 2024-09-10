import 'package:flutter/services.dart';

/// A [MethodChannel] used for communication between Flutter and native platforms (Android/iOS).
class ChannelBridge {
  static final ChannelBridge _instance = ChannelBridge._();

  ChannelBridge._();

  /// The [MethodChannel] used to communicate with the native platforms.
  final MethodChannel channel = const MethodChannel('nativeBridgeChannel');

  static ChannelBridge get instance => _instance;
}
