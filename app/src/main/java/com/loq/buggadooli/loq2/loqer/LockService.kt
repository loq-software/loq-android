package com.loq.buggadooli.loq2.loqer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.loq.buggadooli.loq2.notifications.Notifications

import org.koin.android.ext.android.inject

class LockService : Service() {

    private val notifications by inject<Notifications>()
    private val loqerManager by inject<LoqerManager>()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        loqerManager.scheduleMethod()
        startForeground(1, notifications.buildNotification(this))
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent(this, LockServiceBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
    }
}
