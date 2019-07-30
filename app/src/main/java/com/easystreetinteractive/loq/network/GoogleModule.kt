package com.easystreetinteractive.loq.network

import android.app.Application
import com.easystreetinteractive.loq.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.koin.dsl.module.module

val googleModule = module {

    single {
        val context = get<Application>()
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
    }
}