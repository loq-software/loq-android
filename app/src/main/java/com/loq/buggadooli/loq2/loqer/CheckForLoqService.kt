package com.loq.buggadooli.loq2.loqer

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.loq.buggadooli.loq2.notifications.Notifications

import org.koin.android.ext.android.inject

class CheckForLoqService : Service(), LifecycleOwner {

    private val notifications by inject<Notifications>()
    private val loqerManager by inject<LoqerManager>()

    private val lifecycle = LifecycleRegistry(this)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        loqerManager.scheduleMethod(this@CheckForLoqService, this)
        startForeground(1, notifications.buildNotification(this))
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        loqerManager.onDestroy()
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    companion object {

        fun getIntent(activity: Activity): Intent? {
            return Intent(activity, CheckForLoqService::class.java)
        }
    }
}
