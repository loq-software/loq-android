package com.loq.buggadooli.loq2.permissions

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.loq.buggadooli.loq2.extensions.asString
import io.reactivex.Single.just

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.content.Context.APP_OPS_SERVICE
import android.app.AppOpsManager
import android.os.Process.myUid
import androidx.core.app.AppOpsManagerCompat.MODE_ALLOWED


data class GrantResult(
        val permission: String,
        val granted: Boolean
)

/**
 * Helps us manage, check, and dispatch permission requests without much boiler plate in our Activities
 * or views.
 */
interface PermissionsManager {

    fun onGrantResult(): Observable<GrantResult>

    fun attach(activity: Activity)

    fun hasStoragePermission(): Boolean

    fun hasUsageStatsPermission(): Boolean

    fun requestStoragePermission(waitForGranted: Boolean = false): Single<GrantResult>

    fun processResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

    fun detach(activity: Activity)
}

class RealPermissionsManager(
        private val context: Application,
        private val mainScheduler: Scheduler
) : PermissionsManager {

    companion object {
        @VisibleForTesting(otherwise = PRIVATE)
        const val REQUEST_CODE_STORAGE = 69
    }

    @VisibleForTesting(otherwise = PRIVATE)
    var activity: Activity? = null
    private val relay = PublishSubject.create<GrantResult>()

    override fun onGrantResult(): Observable<GrantResult> = relay.share().observeOn(mainScheduler)

    override fun attach(activity: Activity) {
        Log.d("RealPermissionsManager","attach(): $activity")
        this.activity = activity
    }

    override fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), context.packageName)
        return mode == MODE_ALLOWED
    }

    override fun hasStoragePermission() = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun requestStoragePermission(waitForGranted: Boolean) =
            requestPermission(REQUEST_CODE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, waitForGranted)

    override fun processResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d("","processResult(): requestCode= $requestCode, permissions: ${permissions.asString()}, grantResults: ${grantResults.asString()}")
        for ((index, permission) in permissions.withIndex()) {
            val granted = grantResults[index] == PERMISSION_GRANTED
            val result = GrantResult(permission, granted)
            Log.d("RealPermissionsManager","Permission grant result: $result")
            relay.onNext(result)
        }
    }

    override fun detach(activity: Activity) {
        // === is referential equality - returns true if they are the same instance
        if (this.activity === activity) {
            Log.d("RealPermissionsManager","detach(): $activity")
            this.activity = null
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
    }

    private fun requestPermission(code: Int, permission: String, waitForGranted: Boolean): Single<GrantResult> {
        Log.d("PermissionsManager","Requesting permission: $permission")
        if (hasPermission(permission)) {
            Log.d("PermissionsManager","Already have this permission!")
            return just(GrantResult(permission, true).also {
                relay.onNext(it)
            })
        }

        val attachedTo = activity ?: throw IllegalStateException("Not attached")
        ActivityCompat.requestPermissions(attachedTo, arrayOf(permission), code)
        return onGrantResult()
                .filter { it.permission == permission }
                .filter {
                    if (waitForGranted) {
                        // If we are waiting for granted, only allow emission if granted is true
                        it.granted
                    } else {
                        // Else continue
                        true
                    }
                }
                .take(1)
                .singleOrError()
    }
}
