#include "include/ms_auth_login/ms_auth_login_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "ms_auth_login_plugin.h"

void MsAuthLoginPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  ms_auth_login::MsAuthLoginPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
