package com.easystreetinteractive.loq.loqer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.easystreetinteractive.loq.notifications.Notifications

import org.koin.android.ext.android.inject

class CheckForLoqService : Service(), LifecycleOwner {

    private val notifications by inject<Notifications>()
    private val loqerManager by inject<LoqerManager>()

    private val lifecycle = LifecycleRegistry(this)

    override fun onCreate() {
        super.onCreate()
        lifecycle.currentState = Lifecycle.State.RESUMED
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            try {
                loqerManager.scheduleMethod(this@CheckForLoqService, this)
                startForeground(1, notifications.buildNotification(this))
            }
            catch (exception: Exception){
                exception.printStackTrace()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        lifecycle.currentState = Lifecycle.State.DESTROYED
        stopSelf()
        super.onDestroy()

    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    companion object {

        fun getIntent(context: Context): Intent? {
            return Intent(context, CheckForLoqService::class.java)
        }
    }
}
