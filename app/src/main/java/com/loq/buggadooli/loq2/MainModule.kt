package com.loq.buggadooli.loq2

import android.app.Application
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.dsl.module.module

const val MAIN = "main"

val mainModule = module {

    single {
        get<Application>().packageManager
    }

    factory(name = MAIN) {
        AndroidSchedulers.mainThread()
    }

}