package com.loq.buggadooli.loq2

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.loq.buggadooli.loq2.loqer.loqerModule
import com.loq.buggadooli.loq2.network.googleModule
import com.loq.buggadooli.loq2.network.networkModule
import com.loq.buggadooli.loq2.notifications.notificationsModule
import com.loq.buggadooli.loq2.permissions.permissionsModule
import com.loq.buggadooli.loq2.repositories.repositoriesModule
import com.loq.buggadooli.loq2.ui.viewmodels.viewModelsModule
import com.testfairy.TestFairy
import org.koin.android.ext.android.startKoin

class LoqApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val modules = listOf(
                mainModule,
                networkModule,
                googleModule,
                viewModelsModule,
                notificationsModule,
                repositoriesModule,
                loqerModule,
                permissionsModule
        )
        startKoin(
                androidContext = this,
                modules = modules
        )

        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)

        TestFairy.begin(this, "SDK-eTDNMh8y");
    }
}