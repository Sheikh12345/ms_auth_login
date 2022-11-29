import 'package:flutter_test/flutter_test.dart';
import 'package:ms_auth_login/ms_auth_login.dart';
import 'package:ms_auth_login/ms_auth_login_platform_interface.dart';
import 'package:ms_auth_login/ms_auth_login_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMsAuthLoginPlatform
    with MockPlatformInterfaceMixin
    implements MsAuthLoginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final MsAuthLoginPlatform initialPlatform = MsAuthLoginPlatform.instance;

  test('$MethodChannelMsAuthLogin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMsAuthLogin>());
  });

  test('getPlatformVersion', () async {
    MsAuthLogin msAuthLoginPlugin = MsAuthLogin();
    MockMsAuthLoginPlatform fakePlatform = MockMsAuthLoginPlatform();
    MsAuthLoginPlatform.instance = fakePlatform;

    expect(await msAuthLoginPlugin.getPlatformVersion(), '42');
  });
}
