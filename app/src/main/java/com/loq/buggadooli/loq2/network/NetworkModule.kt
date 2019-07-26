package com.loq.buggadooli.loq2.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.loq.buggadooli.loq2.network.api.AuthenticationService
import com.loq.buggadooli.loq2.network.api.LoqService
import com.loq.buggadooli.loq2.network.api.RealAuthenticationService
import com.loq.buggadooli.loq2.network.api.RealLoqService
import org.koin.dsl.module.module

val networkModule = module {

    single {
        RealAuthenticationService(get(), get())
    } bind AuthenticationService::class

    single {
        FirebaseAuth.getInstance()
    }

    single {
        val database = FirebaseDatabase.getInstance()
        RealLoqService(database.getReference("loqs"))
    } bind LoqService::class

    single {
        FirebaseDatabase.getInstance()
    }
}