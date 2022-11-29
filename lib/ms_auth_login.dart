import 'package:ms_auth_login/login_model.dart';

import 'ms_auth_login_platform_interface.dart';

class MsAuthLogin {
  Future<String?> getPlatformVersion() {
    return MsAuthLoginPlatform.instance.getPlatformVersion();
  }

 static Future<LoginModel> init(
      {required String? clientId,
      required String? redirectUri,
      required String? tenantId,
      required String? resourceId}) async {
    return MsAuthLoginPlatform.instance.init(
        clientId: clientId,
        redirectUri: redirectUri,
        tenantId: tenantId,
        resourceId: resourceId);
  }

  static Future<LoginModel> login() async {
    return MsAuthLoginPlatform.instance.login();
  }

  static Future<LoginModel> silentToken() async {
    return MsAuthLoginPlatform.instance.silentToken();
  }

  static Future<LoginModel> signOut() async {
    return MsAuthLoginPlatform.instance.logout();
  }
}
