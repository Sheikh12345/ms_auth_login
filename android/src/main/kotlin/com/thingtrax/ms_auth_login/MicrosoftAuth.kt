package com.thingtrax.ms_auth_login

import android.app.Activity
import android.content.Context
import android.util.Log
import com.microsoft.identity.client.*
import com.microsoft.identity.client.IPublicClientApplication.ISingleAccountApplicationCreatedListener
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.CurrentAccountCallback
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.SignOutCallback
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import com.microsoft.identity.client.exception.MsalUiRequiredException
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

class MicrosoftAuth {


    companion object SingletonObject{
        val instance = MicrosoftAuth()
        var s: String? = null
        lateinit var scopeOfMS:String;

        val TAG = "Main Activity"
        private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
        private var mAccount: IAccount? = null

        private fun getScopes(): Array<String?> {
            return scopeOfMS.lowercase(Locale.getDefault())
                .split(" ").toTypedArray()
        }

        private fun getAuthInteractiveCallback(callBack:(LoginModel)->Unit): AuthenticationCallback {
            return object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    /* Successfully got a token, use it to call a protected resource - MSGraph */
                    Log.d(TAG, "Successfully authenticated")
                    callBack(LoginModel(error = false,message = "Login is successfully done",token = authenticationResult.accessToken))
                    authenticationResult.account.idToken
                    Log.d(
                        TAG,
                        "Graph token => " + authenticationResult.accessToken + " =End"
                    )
                    mAccount = authenticationResult.account
                }

                override fun onError(exception: MsalException) {
                    Log.d(TAG, "Authentication failed: $exception")
                    callBack(LoginModel(error = true,message = exception.message,token = null))
                }

                override fun onCancel() {
                    /* User canceled the authentication */
                    Log.d(TAG, "User cancelled login.")
                }
            }
        }


        private fun loadAccount() {
            if (mSingleAccountApp == null) {
                return
            }
            mSingleAccountApp!!.getCurrentAccountAsync(object : CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    // You can use the account data to update your UI or your app database.
                    mAccount = activeAccount
                }

                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                    if (currentAccount == null) {
                        // Perform a cleanup task as the signed-in account changed.
                    }
                }

                override fun onError(exception: MsalException) {
                    displayError(exception)
                }
            })
        }

        private fun displayError(exception: Exception) {
            println("Output => $exception")
        }

        private fun getAuthSilentCallback(callBack:(LoginModel)->Unit): SilentAuthenticationCallback {
            return object : SilentAuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    Log.d(TAG, "Graph token => " + authenticationResult.accessToken)
                    callBack(LoginModel(error = false,message = "Silent token fetched",token = authenticationResult.accessToken))
                    /* Successfully got a token, use it to call a protected resource - MSGraph */
                }

                override fun onError(exception: MsalException) {
                    /* Failed to acquireToken */
                    callBack(LoginModel(error = true,message = exception.message,token = null))
                    Log.d(TAG, "Authentication failed: $exception")

                    displayError(exception)
                    if (exception is MsalClientException) {
                        /* Exception inside MSAL, more info inside MsalError.java */
                    } else if (exception is MsalServiceException) {
                        /* Exception when communicating with the STS, likely config issue */
                    } else if (exception is MsalUiRequiredException) {
                        /* Tokens expired or no session, retry with interactive */
                    }
                }
            }
        }

        fun mCreateAndSaveFile(params: String, resourceId:String, context: MsAuthLoginPlugin) {

            try {
                scopeOfMS = resourceId
                val file = FileWriter(
                    "/data/data/" + context.getPackageName().toString() + "/" +"config.json"
                )


                file.write(params)
                file.flush()
                file.close()
            } catch (e: IOException) {
//                print("File path error "+e.message);
                e.printStackTrace()
            }
        }

        fun init(context: MsAuthLoginPlugin, callBack:(LoginModel)->Unit): Boolean {


            val result = BooleanArray(1)
            PublicClientApplication.createSingleAccountPublicClientApplication(context,   File("/data/data/" + context.getPackageName().toString() + "/" +"config.json"),
                object : ISingleAccountApplicationCreatedListener {
                    override fun onCreated(application: ISingleAccountPublicClientApplication) {

                        mSingleAccountApp = application
                        callBack(LoginModel(error = false,message = "initialized data",token = null))
                        result[0] = true
                        loadAccount()
                    }

                    override fun onError(exception: MsalException) {
                        callBack(LoginModel(error = false,message = exception.message,token = null))
                        result[0] = false
                    }
                })
            return result[0]
        }

        fun signIn(context: MsAuthLoginPlugin, callBack:(LoginModel)->Unit) {
            if (mSingleAccountApp != null && scopeOfMS!=null) {
                fun getResult(string:LoginModel){
                    callBack(string)
                }

                mSingleAccountApp!!.signIn(
                    context, null, getScopes(),
                    getAuthInteractiveCallback(::getResult)
                )

            }else if(scopeOfMS==null){
                callBack(LoginModel(error = true,message = "Resource id not found",token = null))
            } else {
                callBack(LoginModel(error = true,message = "Not initialized yet",token = null))
            }
        }
        fun silentToken(context: MsAuthLoginPlugin, callBack:(LoginModel)->Unit) {
            fun getResult(string:LoginModel){
                callBack(string)
            }

            /**
             * Once you've signed the user in,
             * you can perform acquireTokenSilent to obtain resources without interrupting the user.
             */
            if(mSingleAccountApp!=null && mAccount!=null){
                mSingleAccountApp!!.acquireTokenSilentAsync(
                    getScopes(),
                    mAccount!!.authority,
                    getAuthSilentCallback(::getResult))
            }else{
                callBack(LoginModel(error = true,message = "Account not found",token = null))
            }
        }

        fun signOut(context: MsAuthLoginPlugin, callBack:(LoginModel)->Unit) {
            try{
                if(mSingleAccountApp!=null){
                    mSingleAccountApp!!.signOut(object : SignOutCallback {
                        override fun onSignOut() {
                            mAccount = null
                            callBack(LoginModel(error = false,message = "Successfully logout",token = null))
                        }

                        override fun onError(exception: MsalException) {
                            callBack(LoginModel(error = true,message = exception.message,token = null))
                            displayError(exception)
                        }
                    })
                }else{
                    callBack(LoginModel(error = false,message = "Not initialized",token = null))

                }

            }catch (e:Exception){
                callBack(LoginModel(error = true,message = e.message,token = null))
                displayError(e)
            }
        }
    }

}