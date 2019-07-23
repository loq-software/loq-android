package com.loq.buggadooli.loq2.repositories

import android.app.ActivityManager
import android.app.Application
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.CheckBox
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.extensions.toJson
import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.utils.Utils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_lock.*
import java.util.*
import kotlin.collections.ArrayList

interface ApplicationsRepository{

    fun getInstalledApps(): Observable<List<ApplicationInfo>>

    fun getForegroundApp(): String

    fun getHasApplicationInstalled(appName: String): Observable<HasApplicationResult>

    fun saveApplication(
            applicationName: String,
            days: List<CheckBox>,
            startTime: String,
            endTime: String,
            rawStartMinute: String,
            rawEndMinute: String,
            rawStartHour: String,
            rawEndHour: String
    ): Observable<Loq>

    fun saveBlockedApp(blockedApp: Loq): Observable<Loq>

}

class RealApplicationsRepository(private val context: Application): ApplicationsRepository{

    override fun saveBlockedApp(blockedApp: Loq): Observable<Loq> {
        return Observable.create { emitter ->
            Utils.INSTANCE.saveLoqsToFile(context, blockedApp.toJson())// Todo: Get rid of this. Use Firebase to hold a users blocked apps.
            emitter.onNext(blockedApp)
        }
    }

    override fun saveApplication(applicationName: String,
                                 days: List<CheckBox>,
                                 startTime: String,
                                 endTime: String,
                                 rawStartMinute: String,
                                 rawEndMinute: String,
                                 rawStartHour: String,
                                 rawEndHour: String): Observable<Loq> {

        return getInstalledApps()
                .switchMap { installedApplications ->
                    Observable.create<Loq> { emitter ->
                        val packageManager = context.packageManager
                        for (installedApplication in installedApplications){
                            val installedApplicationName = installedApplication.loadLabel(packageManager).toString()
                            if (installedApplicationName.contains(applicationName, true)){
                                val selectedDays = ArrayList<String>()
                                var dayString = ""
                                for (day in days){
                                    if (day.isChecked){
                                        val dayText = "${day.text}"
                                        dayString += "$dayText "

                                        selectedDays.add(dayText)
                                    }
                                }

                                val loq = Loq()
                                loq.appName = applicationName
                                loq.packageName = installedApplication.packageName
                                loq.days = selectedDays
                                loq.daysStr = dayString

                                loq.startTime = startTime
                                loq.endTime = endTime
                                loq.rawStartMinute = rawStartMinute
                                loq.rawEndMinute = rawEndMinute
                                loq.rawStartHour = rawStartHour
                                loq.rawEndHour = rawEndHour

                                emitter.onNext(loq)
                                return@create
                            }
                        }
                    }
                }
                .switchMap { loqToSave ->
                    saveBlockedApp(loqToSave)
                }
    }

    override fun getHasApplicationInstalled(appName: String): Observable<HasApplicationResult> {
        return getInstalledApps()
                .switchMap { applications ->
                    Observable.create<HasApplicationResult> { emitter ->
                       for (application in applications){
                           val name = application.loadLabel(context.packageManager).toString()
                           if (name.contains(appName, true)){
                               val response = HasApplicationResult(application, true)
                               emitter.onNext(response)
                               return@create
                           }
                       }
                        emitter.onNext(HasApplicationResult())
                    }
                }
    }

    override fun getInstalledApps(): Observable<List<ApplicationInfo>> {

        return Observable.create { emitter ->
            val apps = ArrayList<ApplicationInfo>()
            val flags = PackageManager.GET_META_DATA or
                    PackageManager.GET_SHARED_LIBRARY_FILES or
                    PackageManager.GET_UNINSTALLED_PACKAGES

            val applications = context.packageManager.getInstalledApplications(flags)
            for (appInfo in applications) {
                if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                } else {
                    apps.add(appInfo)
                }
            }

            emitter.onNext(apps)
        }
    }

    override fun getForegroundApp(): String {
        var currentApp = "NULL"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (appList != null && appList.size > 0) {
                val mySortedMap = TreeMap<Long, UsageStats>()
                for (usageStats in appList) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = mySortedMap[mySortedMap.lastKey()]!!.packageName
                }
            }
        } else {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.runningAppProcesses
            currentApp = tasks[0].processName
        }

        return currentApp
    }

}

data class HasApplicationResult(val info: ApplicationInfo? = null, val hasResult: Boolean = false)
