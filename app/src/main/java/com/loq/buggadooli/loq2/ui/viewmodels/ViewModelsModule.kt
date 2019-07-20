package com.loq.buggadooli.loq2.ui.viewmodels

import org.koin.dsl.module.module

val viewModelsModule = module {

    single {
        MainViewModel(get())
    }

    single {
        LoginViewModel(get())
    }
}