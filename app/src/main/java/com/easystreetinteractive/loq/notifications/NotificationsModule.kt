package com.easystreetinteractive.loq.notifications

import org.koin.dsl.module.module

val notificationsModule = module {

    single {
        RealNotifications()
    } bind(Notifications::class)
}