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

interface LoqerManager{

    fun scheduleMethod(service: Service, owner: LifecycleOwner)

    fun onDestroy()
}

class RealLoqerManager(
        private val preferenceManager: PreferenceManager,
        private val applicationsRepository: ApplicationsRepository,
        private val loqService: LoqService,
        private val authenticationService: AuthenticationService
): LoqerManager {
    private val scheduler = Executors
            .newSingleThreadScheduledExecutor()

    override fun scheduleMethod(service: Service,owner: LifecycleOwner) {

        Observable.interval(0, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap {
                    loqService.getLoqs(authenticationService.getCurrentUser()?.uid?: "")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success -> {
                            val loqs = outcome.data
                            val activityOnTop = applicationsRepository.getForegroundApp()
                            if (isLocked(loqs, activityOnTop)){
                                val dialogIntent = Intent(service, LockScreenActivity::class.java)
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                if (isLockTime(loq) && !hasTemporaryUnlock())
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

    override fun onDestroy() {
        scheduler.shutdown()
    }
}