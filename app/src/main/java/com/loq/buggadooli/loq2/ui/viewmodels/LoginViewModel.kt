package com.loq.buggadooli.loq2.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.network.AuthenticationService

class LoginViewModel(
        private val authenticationService: AuthenticationService
): ViewModel() {

    fun loginWithGmail(){

    }

    fun loginWithFacebook(){

    }

    fun loginWithEmail(email: String, password: String){

    }
}