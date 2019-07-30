package com.easystreetinteractive.loq.repositories

import org.koin.dsl.module.module

val repositoriesModule = module {

    single {
        RealApplicationsRepository(get())
    } bind ApplicationsRepository::class
}