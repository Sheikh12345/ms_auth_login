import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ms_auth_login/ms_auth_login_method_channel.dart';

void main() {
  MethodChannelMsAuthLogin platform = MethodChannelMsAuthLogin();
  const MethodChannel channel = MethodChannel('ms_auth_login');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
