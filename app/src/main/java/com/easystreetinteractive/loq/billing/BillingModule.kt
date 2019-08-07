package com.easystreetinteractive.loq.billing

import org.koin.dsl.module.module

val billingModule = module {

    single {
        RealBillingManager(get())
    } bind BillingManager::class
}