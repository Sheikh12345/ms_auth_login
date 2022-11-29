import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'ms_auth_login_platform_interface.dart';

/// An implementation of [MsAuthLoginPlatform] that uses method channels.
class MethodChannelMsAuthLogin extends MsAuthLoginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('ms_auth_login');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
