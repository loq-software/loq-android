package com.loq.buggadooli.loq2.ui.viewmodels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.attachLifecycle
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.network.AuthenticationResult
import com.loq.buggadooli.loq2.network.AuthenticationService
import com.loq.buggadooli.loq2.network.Outcome

class LoginViewModel(
        private val authenticationService: AuthenticationService
): ViewModel() {

    val signInSuccessful: LiveData<AuthenticationResult> get() = _signInSuccessful
    private var _signInSuccessful = MutableLiveData<AuthenticationResult>()

    fun loginWithGmail(activity: Activity){
        authenticationService.signInWithGoogle(activity)
    }

    fun loginWithFacebook(){
        authenticationService.signInWithFacebook()
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
    }
}