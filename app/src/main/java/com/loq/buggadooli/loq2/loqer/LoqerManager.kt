package com.loq.buggadooli.loq2.loqer

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import com.loq.buggadooli.loq2.extensions.isAppLocked
import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import com.loq.buggadooli.loq2.ui.activities.LockScreenActivity
import com.loq.buggadooli.loq2.utils.Utils
import java.util.ArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface LoqerManager{

    fun checkRunningApps()

    fun scheduleMethod()
}

class RealLoqerManager(
        private val context: Context,
        private val applicationsRepository: ApplicationsRepository
): LoqerManager{

    private var allowUsage = false
    private var loqs: List<Loq> = ArrayList()

    override fun checkRunningApps() {

        if (allowUsage)
            return

        val activityOnTop = applicationsRepository.getForegroundApp()

        // Provide the packagename(s) of apps here, you want to show password activity
        if (loqs.isAppLocked(activityOnTop)) { // you can make this check even better {
            val dialogIntent = Intent(context, LockScreenActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(dialogIntent)
        }
    }

    override fun scheduleMethod() {
        val scheduler = Executors
                .newSingleThreadScheduledExecutor()
        val launched = booleanArrayOf(false)
        scheduler.scheduleAtFixedRate(Runnable {
            if (launched[0]) return@Runnable
            launched[0] = true
        /*    loqs = Utils.INSTANCE.readLoqsFromFile(context)
            if (Utils.INSTANCE.shouldPause(context))
                allowUsage(60000)

            checkRunningApps()*/ // Todo: Pull apps from firebase
            //}
        }, 1000, 3000, TimeUnit.MILLISECONDS)
    }

    private fun allowUsage(timeInMillis: Long) {
        allowUsage = true
        object : CountDownTimer(timeInMillis, 10000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                allowUsage = false
            }
        }.start()
    }
}