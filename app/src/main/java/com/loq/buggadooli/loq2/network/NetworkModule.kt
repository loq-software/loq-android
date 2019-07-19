package com.loq.buggadooli.loq2.network

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module.module

val networkModule = module {

    single {
        RealAuthenticationService(get(), get(), get())
    } bind AuthenticationService::class

    single {
        RealGmailService(get())
    } bind GmailService::class

    single {
        RealFacebookService()
    } bind FacebookService::class

    single {
        FirebaseAuth.getInstance()
    }

    single {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        GoogleSignIn.getClient(get(), gso)
    }
}