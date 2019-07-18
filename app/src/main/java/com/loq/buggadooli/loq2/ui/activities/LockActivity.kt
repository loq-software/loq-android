package com.loq.buggadooli.loq2.ui.activities

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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.TimePicker

import com.loq.buggadooli.loq2.loqer.LockService
import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.utils.Utils
import com.loq.buggadooli.loq2.ui.widgets.MultiSpinner

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar

class LockActivity : AppCompatActivity(), MultiSpinner.MultiSpinnerListener, TimePickerDialog.OnTimeSetListener {

    private var txtLoqs: TextView? = null
    private var btnFinished: Button? = null
    private var btnAddLoq: Button? = null
    private var btnStartTime: Button? = null
    private var btnEndTime: Button? = null
    private val newLoqs = ArrayList<Loq>()
    private var chooseApps = false
    private var multiSpinner: MultiSpinner? = null
    private var isStartTime = false
    private var apps: List<ApplicationInfo>? = null
    private var days: MutableList<CheckBox> = ArrayList()
    private var mLockService: LockService? = null
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

            val pm = packageManager
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
        setContentView(R.layout.activity_lock)
        supportActionBar!!.hide()
        chooseApps = intent.getBooleanExtra("chooseApps", false)
        getDayCheckboxes()
        txtLoqs = findViewById(R.id.txtLoqs)
        btnFinished = findViewById(R.id.btnFinished)
        btnAddLoq = findViewById(R.id.btnAddLoq)
        btnAddLoq!!.setOnClickListener { addLoq() }
        btnStartTime = findViewById(R.id.btnStartTime)
        btnStartTime!!.setOnClickListener {
            isStartTime = true
            showTimePicker()
        }
        btnEndTime = findViewById(R.id.btnEndTime)
        btnEndTime!!.setOnClickListener {
            isStartTime = false
            showTimePicker()
        }
        btnFinished!!.setOnClickListener {
            if (saveLoqs()) {
                startLockService()
                goToCongratulations()
            }
        }
        multiSpinner = findViewById(R.id.multiSpinner)
        populateAppList()
        if (!verifyAccess()) {
            val builder = AlertDialog.Builder(this)
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
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            return mode == AppOpsManager.MODE_ALLOWED

        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }

    private fun getDayCheckboxes() {
        days.add(findViewById<View>(R.id.chkSunday) as CheckBox)
        days.add(findViewById<View>(R.id.chkMonday) as CheckBox)
        days.add(findViewById<View>(R.id.chkTuesday) as CheckBox)
        days.add(findViewById<View>(R.id.chkWednesday) as CheckBox)
        days.add(findViewById<View>(R.id.chkThursday) as CheckBox)
        days.add(findViewById<View>(R.id.chkFriday) as CheckBox)
        days.add(findViewById<View>(R.id.chkSaturday) as CheckBox)
    }

    private fun saveLoqs(): Boolean {
        val jsonLoqs = convertLoqsToJson()
        Utils.INSTANCE.saveLoqsToFile(applicationContext, jsonLoqs)
        return true
    }

    private fun goToCongratulations() {
        val intent = Intent(applicationContext, ChildLock::class.java)
        startActivity(intent)
    }

    private fun populateAppList() {
        if (!chooseApps) {
            multiSpinner!!.setItems(Arrays.asList(*resources.getStringArray(R.array.popular_apps_2)), "", this)
            apps = listOfInstalledApps
        } else {
            apps = listOfInstalledApps
            val appNames = ArrayList<String>()
            var nameVal: String?
            for (appInfo in apps!!) {
                nameVal = appInfo.loadLabel(packageManager).toString()
                if (nameVal != null) {
                    appNames.add(nameVal)
                }
            }
            multiSpinner!!.setItems(appNames, "", this)
        }
    }

    private fun showTimePicker() {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val dialog = TimePickerDialog(this, this, hour, minute,
                DateFormat.is24HourFormat(this))
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
            appName = appInfo.loadLabel(packageManager).toString()
            if (selectedApps.contains(appName)) {
                val loq = Loq()
                loq.appName = appName
                loq.packageName = appInfo.packageName
                loq.Days = selectedDays
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
            for (day in loq.Days!!) {
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
                obj.put("Days", daysStr)
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
        mLockService = LockService()
        if (isMyServiceRunning(mLockService!!.javaClass)) {
            //stop and restart
        } else {
            mServiceIntent = Intent(applicationContext, mLockService!!.javaClass)
            val pintent = PendingIntent.getService(this, 0, mServiceIntent!!, 0)
            val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, 1000, pintent)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
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
            stopService(mServiceIntent)
        super.onDestroy()

    }

}
