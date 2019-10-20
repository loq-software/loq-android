package com.easystreetinteractive.loq.loqer

import android.app.Service
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import com.easystreetinteractive.loq.extensions.attachLifecycle
import com.easystreetinteractive.loq.extensions.isLockTime
import com.easystreetinteractive.loq.extensions.subscribeForOutcome
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.network.Outcome
import com.easystreetinteractive.loq.network.api.AuthenticationService
import com.easystreetinteractive.loq.network.api.LoqService
import com.easystreetinteractive.loq.preferences.PreferenceManager
import com.easystreetinteractive.loq.repositories.ApplicationsRepository
import com.easystreetinteractive.loq.ui.activities.LockScreenActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import android.app.Activity
import androidx.core.content.ContextCompat.getSystemService
import android.app.ActivityManager
import android.content.Context
import android.app.ActivityManager.RunningAppProcessInfo
import androidx.core.content.ContextCompat.getSystemService




interface LoqerManager{

    fun scheduleMethod(service: Service, owner: LifecycleOwner)

}

class RealLoqerManager(
        private val preferenceManager: PreferenceManager,
        private val applicationsRepository: ApplicationsRepository,
        private val loqService: LoqService,
        private val authenticationService: AuthenticationService,
        private val context: Context
): LoqerManager {

    override fun scheduleMethod(service: Service,owner: LifecycleOwner) {

        Observable.interval(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap {
                    loqService.getLoqs(authenticationService.getCurrentUser()?.uid?: "")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success -> {
                            val loqs = outcome.data
                            val activityOnTop = applicationsRepository.getForegroundAppPackageName()?: return@subscribeForOutcome
                            if (isLocked(loqs, activityOnTop)){

                                val dialogIntent = Intent(context, LockScreenActivity::class.java)
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_NEW_TASK)
                                service.startActivity(dialogIntent)
                            }
                        }
                    }
                }
                .attachLifecycle(owner)
    }

    private fun isLocked(blockedApplications: List<BlockedApplication>, packageName: String): Boolean {
        for (loq in blockedApplications) {
            if (loq.applicationName.equals(packageName, ignoreCase = true)
                    || loq.packageName.equals(packageName, ignoreCase = true)) {
                if (loq.isLockTime() && !hasTemporaryUnlock())
                    return true
            }
        }
        return false
    }

    private fun hasTemporaryUnlock(): Boolean {
        val unlockTime = preferenceManager.temporaryUnlockTime
        val currentTime = System.currentTimeMillis()
        return currentTime - unlockTime < 60000
    }

}