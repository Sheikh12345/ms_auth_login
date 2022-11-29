import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:ms_auth_login/login_model.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'ms_auth_login_method_channel.dart';

abstract class MsAuthLoginPlatform extends PlatformInterface {
  /// Constructs a MsAuthLoginPlatform.
  MsAuthLoginPlatform() : super(token: _token);

  static final Object _token = Object();
  final methodChannel = const MethodChannel('ms_auth_login');
  static MsAuthLoginPlatform _instance = MethodChannelMsAuthLogin();

  /// The default instance of [MsAuthLoginPlatform] to use.
  ///
  /// Defaults to [MethodChannelMsAuthLogin].
  static MsAuthLoginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MsAuthLoginPlatform] when
  /// they register themselves.
  static set instance(MsAuthLoginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<LoginModel> init(
      {required String? clientId,
      required String? redirectUri,
      required String? tenantId,
      required String? resourceId}) async {
    if (clientId == null) {
      return const LoginModel(true, "Client id is missing", "");
    } else if (redirectUri == null) {
      return const LoginModel(true, "Redirect URI is missing", "");
    } else if (tenantId == null) {
      return const LoginModel(true, "Tenant ID is missing", "");
    } else if (resourceId == null) {
      return const LoginModel(true, "Resource ID is missing", "");
    } else {
      dynamic result = await methodChannel.invokeMethod(
          'init',
          json.encode({
            "client_id": clientId,
            "redirect_uri": redirectUri,
            "tenant_id": tenantId,
            "resource_id": resourceId
          }));
      Map map = json.decode(result);
      if (map.keys.any((element) => element == 'token')) {
        return LoginModel(map['error'], map["message"], map["token"]);
      } else {
        return LoginModel(map['error'], map["message"], "");
      }
    }
  }

  Future<LoginModel> login() async {
    dynamic result = await methodChannel.invokeMethod('login');
    Map map = json.decode(result);
    if (map.keys.any((element) => element == 'token')) {
      return LoginModel(map['error'], map["message"], map["token"]);
    } else {
      return LoginModel(map['error'], map["message"], "");
    }
  }

  Future<LoginModel> silentToken() async {
    dynamic result = await methodChannel.invokeMethod('silentToken');
    Map map = json.decode(result);
    if (map.keys.any((element) => element == 'token')) {
      return LoginModel(map['error'], map["message"], map["token"]);
    } else {
      return LoginModel(map['error'], map["message"], "");
    }
  }

  Future<LoginModel> logout() async {
    dynamic result = await methodChannel.invokeMethod('signOut');
    Map map = json.decode(result);
    if (map.keys.any((element) => element == 'token')) {
      return LoginModel(map['error'], map["message"], map["token"]);
    } else {
      return LoginModel(map['error'], map["message"], "");
    }
  }
}
