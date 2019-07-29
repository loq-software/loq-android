package com.loq.buggadooli.loq2.permissions

import com.loq.buggadooli.loq2.MAIN
import org.koin.dsl.module.module

val permissionsModule = module {

    single {
        RealPermissionsManager(get(), get(name = MAIN))
    } bind PermissionsManager::class
}