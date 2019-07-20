package com.loq.buggadooli.loq2.network

import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module.module

val networkModule = module {

    single {
        RealAuthenticationService(get(), get())
    } bind AuthenticationService::class

    single {
        FirebaseAuth.getInstance()
    }
}