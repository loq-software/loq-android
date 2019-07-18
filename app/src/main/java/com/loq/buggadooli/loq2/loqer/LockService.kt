package com.loq.buggadooli.loq2.loqer

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import com.loq.buggadooli.loq2.notifications.Notifications

import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import com.loq.buggadooli.loq2.ui.activities.LockScreenActivity
import com.loq.buggadooli.loq2.utils.Utils
import org.koin.android.ext.android.inject

import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
