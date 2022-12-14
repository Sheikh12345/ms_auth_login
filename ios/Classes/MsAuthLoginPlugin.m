#import "MsAuthLoginPlugin.h"
#if __has_include(<ms_auth_login/ms_auth_login-Swift.h>)
#import <ms_auth_login/ms_auth_login-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "ms_auth_login-Swift.h"
#endif

@implementation MsAuthLoginPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMsAuthLoginPlugin registerWithRegistrar:registrar];
}
@end
