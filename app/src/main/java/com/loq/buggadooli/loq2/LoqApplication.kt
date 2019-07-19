package com.loq.buggadooli.loq2

import android.app.Application
import com.loq.buggadooli.loq2.loqer.loqerModule
import com.loq.buggadooli.loq2.network.googleModule
import com.loq.buggadooli.loq2.network.networkModule
import com.loq.buggadooli.loq2.notifications.notificationsModule
import com.loq.buggadooli.loq2.repositories.repositoriesModule
import com.loq.buggadooli.loq2.ui.viewmodels.viewModelsModule
import org.koin.android.ext.android.startKoin

class LoqApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val modules = listOf(
                mainModule,
                viewModelsModule,
                notificationsModule,
                repositoriesModule,
                loqerModule,
                networkModule,
                googleModule
        )
        startKoin(
                androidContext = this,
                modules = modules
        )
    }
}