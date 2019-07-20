package com.loq.buggadooli.loq2.ui.viewmodels

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.CallbackManager
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.attachLifecycle
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.network.AuthenticationResult
import com.loq.buggadooli.loq2.network.AuthenticationService
import com.loq.buggadooli.loq2.network.Outcome
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.login.LoginManager



class LoginViewModel(
        private val authenticationService: AuthenticationService
): ViewModel() {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    val signInSuccessful: LiveData<AuthenticationResult> get() = _signInSuccessful
    private val _signInSuccessful = MutableLiveData<AuthenticationResult>()

    val signInError: LiveData<String> get() = _signInError
    private val _signInError = MutableLiveData<String>()

    fun loginWithGmail(activity: Activity){
        authenticationService.signInWithGoogle(activity)
    }

    fun loginWithFacebook(activity: Activity, owner: LifecycleOwner){
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        authenticationService.handleFacebookAccessToken(loginResult.accessToken)
                                .ioToMain()
                                .subscribeForOutcome { outcome ->
                                    when(outcome){
                                        is Outcome.Success ->{
                                            _signInSuccessful.postValue(outcome.data)
                                        }
                                        else -> {
                                            _signInSuccessful.postValue(AuthenticationResult())
                                        }
                                    }
                                }.attachLifecycle(owner)
                    }

                    override fun onCancel() {
                        Log.d("test", "facebook login can canceled")
                    }

                    override fun onError(exception: FacebookException) {
                        Log.d("test", exception.message)
                        _signInError.postValue(exception.message)
                    }
                })

        authenticationService.signInWithFacebook(activity)
    }

    fun loginWithEmail(email: String, password: String, owner: LifecycleOwner){

        authenticationService.signInWithEmailAndPassword(email, password)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            _signInSuccessful.postValue(outcome.data)
                        }
                        else -> {
                            _signInSuccessful.postValue(AuthenticationResult())
                        }
                    }
                }.attachLifecycle(owner)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, owner: LifecycleOwner) {
        if (requestCode == Constants.GOOGLE_LOGIN_REQUEST) {
            authenticationService.handleGoogleLogin(data)
                    .ioToMain()
                    .subscribeForOutcome { outcome ->
                        when(outcome){
                            is Outcome.Success ->{
                                _signInSuccessful.postValue(outcome.data)
                            }
                            else -> {
                                _signInSuccessful.postValue(AuthenticationResult())
                            }
                        }
                    }.attachLifecycle(owner)
        }

        else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}