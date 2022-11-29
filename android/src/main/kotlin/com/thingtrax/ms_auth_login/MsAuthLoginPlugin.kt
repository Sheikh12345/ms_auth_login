package com.thingtrax.ms_auth_login

import androidx.annotation.NonNull
import com.google.gson.Gson

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** MsAuthLoginPlugin */
class MsAuthLoginPlugin: FlutterPlugin, MethodCallHandler {
  val gson = Gson()

  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "ms_auth_login")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if(call.method == "init"){
      var jsonString =  call.arguments<String>();
      val clientData: CredentialsData = gson.fromJson(jsonString, CredentialsData::class.java)
      print("Data => ${clientData.client_id}");
      MicrosoftAuth.mCreateAndSaveFile(
        "{\n" +
                "  \"client_id\" : \"${clientData.client_id}\",\n" +
                "  \"authorization_user_agent\" : \"DEFAULT\",\n" +
                "  \"account_mode\" : \"SINGLE\",\n" +
                "  \"redirect_uri\" : \"${clientData.redirect_uri}\",\n" +
                "  \"authorities\" : [\n" +
                "    {\n" +
                "      \"type\": \"AAD\",\n" +
                "      \"audience\": {\n" +
                "        \"type\": \"AzureADMyOrg\",\n" +
                "        \"tenant_id\": \"${clientData.tenant_id}\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}",
        "${clientData.resource_id}",
        this
      )

      fun resultBack(string:LoginModel){
        println("Callback kotlin:: $string");
        result.success(gson.toJson(string))
      }

      MicrosoftAuth.init(this,::resultBack)

    } else if(call.method == "login"){

      fun returnAccessToken(string:LoginModel){
        println("Callback kotlin:: $string");
        result.success(gson.toJson(string))
      }

      MicrosoftAuth.signIn(this,::returnAccessToken)
    } else if(call.method == "silentToken"){

      fun returnSilentToken(string:LoginModel){
        println("Callback kotlin:: $string");
        result.success(gson.toJson(string))
      }


      MicrosoftAuth.silentToken(this,::returnSilentToken)
    }else if(call.method == "signOut"){
      fun returnSilentToken(string:LoginModel){
        println("Callback kotlin:: $string");
        result.success(gson.toJson(string))
      }
      MicrosoftAuth.signOut(this,::returnSilentToken)
    }else{
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
