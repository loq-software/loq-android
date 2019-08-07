package com.easystreetinteractive.loq.ads

import org.koin.dsl.module.module

val adModule = module {

    single {
        RealAdManager()
    } bind AdManager::class
}