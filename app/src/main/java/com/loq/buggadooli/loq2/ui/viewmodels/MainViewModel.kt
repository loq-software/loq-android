package com.loq.buggadooli.loq2.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.loq.buggadooli.loq2.network.AuthenticationService

class MainViewModel(private val authenticationService: AuthenticationService): ViewModel() {


    val user: FirebaseUser? = authenticationService.currentUser

}