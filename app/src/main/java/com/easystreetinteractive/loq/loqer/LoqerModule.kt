package com.easystreetinteractive.loq.loqer

import org.koin.dsl.module.module

val loqerModule = module {

    single {
        RealLoqerManager(get(), get(), get(), get(), get())
    } bind LoqerManager::class
}