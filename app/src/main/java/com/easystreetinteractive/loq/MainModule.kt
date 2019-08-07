package com.easystreetinteractive.loq

import android.app.Application
import android.content.Context
import com.easystreetinteractive.loq.preferences.PreferenceManager
import com.easystreetinteractive.loq.preferences.RealPreferenceManager
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.dsl.module.module

const val MAIN = "main"

val mainModule = module {

    single {
        get<Application>().packageManager
    }

    single {
        RealPreferenceManager(get<Application>().getSharedPreferences("loqPreferences", Context.MODE_PRIVATE))
    } bind PreferenceManager::class

    factory(name = MAIN) {
        AndroidSchedulers.mainThread()
    }

}