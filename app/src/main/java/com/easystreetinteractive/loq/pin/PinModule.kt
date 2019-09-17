package com.easystreetinteractive.loq.pin

import org.koin.dsl.module.module

val pinModule = module {

    single {
        RealPinManager(get())
    } bind PinManager::class
}