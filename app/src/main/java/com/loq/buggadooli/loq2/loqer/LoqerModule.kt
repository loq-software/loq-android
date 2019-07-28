package com.loq.buggadooli.loq2.loqer

import org.koin.dsl.module.module

val loqerModule = module {

    single {
        RealLoqerManager(get(), get(), get(), get())
    } bind LoqerManager::class
}