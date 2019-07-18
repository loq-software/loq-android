package com.loq.buggadooli.loq2.notifications

import org.koin.dsl.module.module

val notificationsModule = module {

    single {
        RealNotifications()
    } bind(Notifications::class)
}