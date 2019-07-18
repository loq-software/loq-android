package com.loq.buggadooli.loq2

import android.app.Application
import org.koin.dsl.module.module

val mainModule = module {

    single {
        get<Application>().packageManager
    }

}