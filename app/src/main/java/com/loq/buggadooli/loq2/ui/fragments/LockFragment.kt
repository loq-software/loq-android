package com.loq.buggadooli.loq2.ui.fragments

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TimePicker
import androidx.fragment.app.Fragment

import com.loq.buggadooli.loq2.loqer.CheckForLoqService
import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.addFragment
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.utils.Utils
import com.loq.buggadooli.loq2.ui.widgets.MultiSpinner
import kotlinx.android.synthetic.main.fragment_lock.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Calendar

class LockFragment : Fragment(), MultiSpinner.MultiSpinnerListener, TimePickerDialog.OnTimeSetListener {

    private val newLoqs = ArrayList<Loq>()
    private var chooseAppName = ""
    private var isStartTime = false
    private var apps: List<ApplicationInfo>? = null
    private var days: MutableList<CheckBox> = ArrayList()
    private var mLockService: CheckForLoqService? = null
    private var mServiceIntent: Intent? = null
    private var rawStartHour: String? = null
    private var rawStartMinute: String? = null
    private var rawEndHour: String? = null
    private var rawEndMinute: String? = null

    private val listOfInstalledApps: List<ApplicationInfo>
        get() {
            val apps = ArrayList<ApplicationInfo>()
            val flags = PackageManager.GET_META_DATA or
                    PackageManager.GET_SHARED_LIBRARY_FILES or
                    PackageManager.GET_UNINSTALLED_PACKAGES

            val pm = safeActivity.packageManager
            val applications = pm.getInstalledApplications(flags)
            for (appInfo in applications) {
                if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                } else {
                    apps.add(appInfo)
                }
            }

            return apps
        }

    private val selectedDays: List<String>
        get() {
            val selectedDays = ArrayList<String>()
            for (item in days) {
                if (item.isChecked)
                    selectedDays.add(item.text.toString())
            }
            return selectedDays
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chooseAppName = arguments?.getString(Constants.APP_NAME, "")?: ""
        this.chooseAppName = chooseAppName
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_lock, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDayCheckboxes()
        btnAddLoq.setOnClickListener { addLoq() }
        btnStartTime!!.setOnClickListener {
            isStartTime = true
            showTimePicker()
        }
        btnEndTime!!.setOnClickListener {
            isStartTime = false
            showTimePicker()
        }
        btnFinished!!.setOnClickListener {
            if (saveLoqs()) {
                startLockService()
                safeActivity.addFragment(fragment = ChildLockFragment()) }
        }
        populateAppList()
        if (!verifyAccess()) {
            val builder = AlertDialog.Builder(safeActivity)
            builder.setMessage("Make sure Usage Access is granted to Loq for the app to work!")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, id ->
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        startActivity(intent)
                    }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun verifyAccess(): Boolean {
        try {
            val packageManager = safeActivity.packageManager
            val applicationInfo = packageManager.getApplicationInfo(safeActivity.packageName, 0)
            val appOpsManager = safeActivity.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            return mode == AppOpsManager.MODE_ALLOWED

        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }

    private fun getDayCheckboxes() {
        days.add(chkSunday)
        days.add(chkMonday)
        days.add(chkTuesday)
        days.add(chkWednesday)
        days.add(chkThursday)
        days.add(chkFriday)
        days.add(chkSaturday)
    }

    private fun saveLoqs(): Boolean {
        val jsonLoqs = convertLoqsToJson()
        Utils.INSTANCE.saveLoqsToFile(safeActivity, jsonLoqs)
        return true
    }

    private fun populateAppList() {
        apps = listOfInstalledApps

        if (chooseAppName.isBlank()) {
            multiSpinner!!.setItems(listOf(*resources.getStringArray(R.array.popular_apps_2)), "", this)
        } else {
            val appNames = ArrayList<String>()
            var nameVal: String?
            for (appInfo in apps!!) {
                nameVal = appInfo.loadLabel(safeActivity.packageManager).toString()
                appNames.add(nameVal)
            }
            multiSpinner!!.setItems(appNames, "", this)
        }
    }

    private fun showTimePicker() {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val dialog = TimePickerDialog(safeActivity, this, hour, minute,
                DateFormat.is24HourFormat(safeActivity))
        dialog.show()
    }

    override fun onItemsSelected(selected: BooleanArray) {

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        var hourOfDay = hourOfDay
        val rawHour = hourOfDay
        var timeOfDay = "AM"
        if (hourOfDay > 11) {
            timeOfDay = "PM"
        }

        if (hourOfDay > 12) {
            hourOfDay -= 12
        }

        val hourStr = hourOfDay.toString()
        val minuteStr = minute.toString()
        if (isStartTime) {
            rawStartHour = rawHour.toString()
            rawStartMinute = minute.toString()
            btnStartTime!!.text = "$hourStr:$minuteStr $timeOfDay"
        } else {
            rawEndHour = rawHour.toString()
            rawEndMinute = minute.toString()
            btnEndTime!!.text = "$hourStr:$minuteStr $timeOfDay"
        }
    }

    private fun addLoq() {
        val selectedApps = multiSpinner!!.selectedItems
        if (selectedApps.isEmpty()) {
            return
        }
        var appName: String
        for (appInfo in apps!!) {
            appName = appInfo.loadLabel(safeActivity.packageManager).toString()
            if (selectedApps.contains(appName)) {
                val loq = Loq()
                loq.appName = appName
                loq.packageName = appInfo.packageName
                loq.days = selectedDays
                loq.startTime = btnStartTime!!.text.toString()
                loq.endTime = btnEndTime!!.text.toString()
                loq.rawEndHour = rawEndHour
                loq.rawStartMinute = rawStartMinute
                loq.rawEndMinute = rawEndMinute
                loq.rawStartHour = rawStartHour
                loq.rawEndHour = rawEndHour
                newLoqs.add(loq)
            }
        }
        addLoqsToOutput()
    }

    private fun addLoqsToOutput() {
        var textVal = ""
        for (loq in newLoqs) {
            var days = ""
            for (day in loq.days!!) {
                days += "$day "
            }
            loq.daysStr = days
            textVal += loq.appName + "  " + days + "  " + loq.startTime + " to " + loq.endTime + "\n"
        }
        txtLoqs!!.text = textVal
    }

    private fun convertLoqsToJson(): String {
        val jsonObj = JSONObject()
        val jsonArr = JSONArray()
        for ((AppName, _, daysStr, startTime, endTime, packageName, rawStartHour1, rawStartMinute1, rawEndHour1, rawEndMinute1) in newLoqs) {
            val obj = JSONObject()
            try {
                obj.put("AppName", AppName)
                obj.put("days", daysStr)
                obj.put("PackageName", packageName)
                obj.put("StartTime", startTime)
                obj.put("EndTime", endTime)
                obj.put("EndHour", rawEndHour1)
                obj.put("EndMinute", rawEndMinute1)
                obj.put("StartHour", rawStartHour1)
                obj.put("StartMinute", rawStartMinute1)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            jsonArr.put(obj)
        }

        try {
            jsonObj.put("Loqs", jsonArr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonObj.toString()
    }

    private fun startLockService() {
        mLockService = CheckForLoqService()
        if (isMyServiceRunning(mLockService!!.javaClass)) {
            //stop and restart
        } else {
            mServiceIntent = Intent(safeActivity, mLockService!!.javaClass)
            val pintent = PendingIntent.getService(safeActivity, 0, mServiceIntent!!, 0)
            val alarm = safeActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, 1000, pintent)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = safeActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }

    override fun onDestroy() {
        if (mServiceIntent != null)
            safeActivity.stopService(mServiceIntent)
        super.onDestroy()

    }

}
