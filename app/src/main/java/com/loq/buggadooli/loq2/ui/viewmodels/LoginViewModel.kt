package com.loq.buggadooli.loq2.ui.viewmodels

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.CallbackManager
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.attachLifecycle
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.network.api.AuthenticationResult
import com.loq.buggadooli.loq2.network.api.AuthenticationService
import com.loq.buggadooli.loq2.network.Outcome
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.login.LoginManager
import com.loq.buggadooli.loq2.extensions.disposeOnDetach
import com.loq.buggadooli.loq2.models.BlockedApplication
import com.loq.buggadooli.loq2.network.api.LoqService
import com.loq.buggadooli.loq2.utils.Event


class LoginViewModel(
        private val authenticationService: AuthenticationService,
        private val loqService: LoqService
): ViewModel() {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    val signInSuccessful: LiveData<Event<AuthenticationResult>> get() = _signInSuccessful
    private val _signInSuccessful = MutableLiveData<Event<AuthenticationResult>>()

    val signInError: LiveData<Event<String>> get() = _signInError
    private val _signInError = MutableLiveData<Event<String>>()

    val onLoqsLoaded: LiveData<Event<List<BlockedApplication>>> get() = _onLoqsLoaded
    private val _onLoqsLoaded = MutableLiveData<Event<List<BlockedApplication>>>()

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
                        _signInError.postValue(Event(exception.message?: "Error"))
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

    fun loadLoqs(view: View) {
        val id = authenticationService.getCurrentUser()?.uid?: return
       loqService.getLoqs(id)
               .ioToMain()
               .subscribeForOutcome { outcome ->
                   when(outcome){
                       is Outcome.Success ->{
                         _onLoqsLoaded.postValue(Event(outcome.data))
                       }
                       is Outcome.ApiError -> {
                           outcome.e.printStackTrace()
                          _showErrorMessage.postValue(Event("Network error"))
                       }
                       is Outcome.Failure -> {
                           outcome.e.printStackTrace()
                           _showErrorMessage.postValue(Event("Error"))
                       }
                   }
               }
               .disposeOnDetach(view)

    }
}