package com.easystreetinteractive.loq.ui.viewmodels

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.CallbackManager
import com.easystreetinteractive.loq.constants.Constants
import com.easystreetinteractive.loq.extensions.attachLifecycle
import com.easystreetinteractive.loq.extensions.ioToMain
import com.easystreetinteractive.loq.extensions.subscribeForOutcome
import com.easystreetinteractive.loq.network.api.AuthenticationResult
import com.easystreetinteractive.loq.network.api.AuthenticationService
import com.easystreetinteractive.loq.network.Outcome
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.login.LoginManager
import com.easystreetinteractive.loq.extensions.disposeOnDetach
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.network.api.LoqService
import com.easystreetinteractive.loq.utils.Event


class LoginViewModel(
        private val authenticationService: AuthenticationService,
        private val loqService: LoqService
): ViewModel() {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    val signInSuccessful: LiveData<Event<AuthenticationResult>> get() = _signInSuccessful
    private val _signInSuccessful = MutableLiveData<Event<AuthenticationResult>>()

    val signInError: LiveData<Event<String>> get() = _signInError
    private val _signInError = MutableLiveData<Event<String>>()

    private val _showErrorMessage = MutableLiveData<Event<String>>()
    val showErrorMessage: LiveData<Event<String>> get() = _showErrorMessage

    fun loginWithGmail(activity: Activity){
        authenticationService.signInWithGoogle(activity)
    }

    fun loginWithFacebook(activity: Activity, owner: LifecycleOwner){
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        authenticationService.handleFacebookLogin(loginResult.accessToken)
                                .ioToMain()
                                .subscribeForOutcome { outcome ->
                                    when(outcome){
                                        is Outcome.Success ->{
                                            _signInSuccessful.postValue(Event(outcome.data))
                                        }
                                        else -> {
                                            _signInError.postValue(Event("Error logging in with Facebook"))
                                        }
                                    }
                                }.attachLifecycle(owner)
                    }

                    override fun onCancel() {
                        Log.d("test", "facebook login is canceled")
                    }

                    override fun onError(exception: FacebookException) {
                        exception.printStackTrace()
                        _signInError.postValue(Event(exception.message
                                ?: "Error"))
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
                            _signInSuccessful.postValue(Event(outcome.data))
                        }
                        else -> {
                            _signInSuccessful.postValue(Event(AuthenticationResult()))
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
                                _signInSuccessful.postValue(Event(outcome.data))
                            }
                            else -> {
                                _signInError.postValue(Event("Error Logging into Gmail"))
                            }
                        }
                    }.attachLifecycle(owner)
        }

        else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}