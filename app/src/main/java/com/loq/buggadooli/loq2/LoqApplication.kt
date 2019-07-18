package com.loq.buggadooli.loq2

import android.app.Application
import com.loq.buggadooli.loq2.ui.viewmodels.viewModelsModule
import org.koin.android.ext.android.startKoin

class LoqApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val modules = listOf(
                viewModelsModule
        )
        startKoin(
                androidContext = this,
                modules = modules
        )
    }
}