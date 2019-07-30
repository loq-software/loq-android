package com.easystreetinteractive.loq.permissions

import com.easystreetinteractive.loq.MAIN
import org.koin.dsl.module.module

val permissionsModule = module {

    single {
        RealPermissionsManager(get(), get(name = MAIN))
    } bind PermissionsManager::class
}