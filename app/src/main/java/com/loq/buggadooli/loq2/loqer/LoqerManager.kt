package com.loq.buggadooli.loq2.loqer

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.loq.buggadooli.loq2.extensions.*
import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.network.api.AuthenticationService
import com.loq.buggadooli.loq2.network.api.LoqService
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import com.loq.buggadooli.loq2.ui.activities.LockScreenActivity
import com.loq.buggadooli.loq2.utils.Utils
import java.util.ArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface LoqerManager{

    fun checkRunningApps()

    fun scheduleMethod(owner: LifecycleOwner)

    fun onDestroy()
}

class RealLoqerManager(
        private val context: Context,
        private val applicationsRepository: ApplicationsRepository,
        private val loqService: LoqService,
        private val authenticationService: AuthenticationService
): LoqerManager{
    val scheduler = Executors
            .newSingleThreadScheduledExecutor()

    private var allowUsage = false
    private var loqs: List<Loq> = ArrayList()

    override fun checkRunningApps() {

        if (allowUsage)
            return

        val activityOnTop = applicationsRepository.getForegroundApp()

       /* // Provide the packagename(s) of apps here, you want to show password activity
        if (loqs.isAppLocked(activityOnTop)) { // you can make this check even better {
            val dialogIntent = Intent(context, LockScreenActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(dialogIntent)
        }*/
    }

    override fun scheduleMethod(owner: LifecycleOwner) {
        scheduler.scheduleAtFixedRate(Runnable {
            val userId = authenticationService.getCurrentUser()?.uid?: return@Runnable
            loqService.getLoqs(userId)
                    .ioToMain()
                    .subscribeForOutcome { outcome ->
                        when(outcome){
                            is Outcome.Success -> {
                                val loqs = outcome.data
                                val activityOnTop = applicationsRepository.getForegroundApp()
                                if (loqs.isLocked(activityOnTop)){
                                    val dialogIntent = Intent(context, LockScreenActivity::class.java)
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(dialogIntent)
                                }
                            }
                        }
                    }
                    .attachLifecycle(owner)
        }, 1000, 10000, TimeUnit.MILLISECONDS)
    }

    override fun onDestroy() {
        scheduler.shutdown()
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