package com.easystreetinteractive.loq.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.easystreetinteractive.loq.network.api.AuthenticationService
import com.easystreetinteractive.loq.network.api.LoqService
import com.easystreetinteractive.loq.network.api.RealAuthenticationService
import com.easystreetinteractive.loq.network.api.RealLoqService
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