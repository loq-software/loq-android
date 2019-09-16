package com.easystreetinteractive.loq.ui.viewmodels

import org.koin.dsl.module.module

val viewModelsModule = module {

    single { MainViewModel(get(), get()) }

    single { LoginViewModel(get(), get()) }

    single { EasyLoqViewModel(get()) }

    single { SetAndForgetViewModel(get(), get(), get()) }

    single { CustomLoqViewModel(get()) }

    single { DashboardViewModel(get(), get()) }

    single { LoqScreenViewModel(get(), get(), get()) }

    single { EditLoqViewModel(get(), get()) }

    single { OneTimeLoqViewModel(get(), get(), get()) }

    single { ConfirmSelectionsViewModel(get(), get(), get()) }
}