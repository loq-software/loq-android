package com.easystreetinteractive.loq

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.easystreetinteractive.loq.loqer.loqerModule
import com.easystreetinteractive.loq.network.googleModule
import com.easystreetinteractive.loq.network.networkModule
import com.easystreetinteractive.loq.notifications.notificationsModule
import com.easystreetinteractive.loq.permissions.permissionsModule
import com.easystreetinteractive.loq.repositories.repositoriesModule
import com.easystreetinteractive.loq.ui.viewmodels.viewModelsModule
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