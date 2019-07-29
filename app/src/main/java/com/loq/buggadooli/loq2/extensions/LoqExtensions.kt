package com.loq.buggadooli.loq2.extensions

import android.content.pm.ApplicationInfo
import android.widget.CheckBox
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.models.BlockedApplication
import com.loq.buggadooli.loq2.models.CustomLoqItem
import com.loq.buggadooli.loq2.models.Loq
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

inline fun List<BlockedApplication>.isLocked(packageName: String): Boolean {
    for (loq in this) {
        if (loq.applicationName.equals(packageName, ignoreCase = true)
                || loq.packageName.equals(packageName, ignoreCase = true)) {
            if (isLockTime(loq))
                return true
        }
    }
    return false
}

inline fun isLockTime(loq: BlockedApplication): Boolean {
    val date = Date()   // given date
    val calendar = GregorianCalendar.getInstance() // creates a new calendar instance
    calendar.time = date   // assigns calendar to given date
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val calenderDay = calendar.get(Calendar.DAY_OF_WEEK)
    val currentDay: String = when(calenderDay){
        Constants.MONDAY -> {
            "Monday"
        }
        Constants.TUESDAY -> {
            "Tuesday"
        }
        Constants.WEDNESDAY -> {
            "Wednesday"
        }
        Constants.THURSDAY -> {
            "Thursday"
        }
        Constants.FRIDAY -> {
            "Friday"
        }
        Constants.SATURDAY -> {
           "Saturday"
        }
        Constants.SUNDAY -> {
            "Sunday"
        }
        else -> {
            "Unknown"
        }
    }

    for (day in loq.blockBlockedDays) {
        if (day.dayOfWeek.contentEquals(currentDay)) {
            val startHour = day.time?.startHour ?: 0
            val endHour = day.time?.endHour ?: 0
            val startMintute = day.time?.startMinute ?: 0
            val endMinute = day.time?.endMinute ?: 0
            if (hour in startHour..endHour) {
                if (hour == startHour && minute < startMintute)
                    continue
                if (hour == endHour && minute > endMinute)
                    continue
                return true
            }
        }
    }
    return false
}

fun Loq.toJson(): String{
    val jsonObj = JSONObject()
    val jsonArr = JSONArray()
    val obj = JSONObject()
    try {
        obj.put("AppName", appName)
        obj.put("days", daysStr)
        obj.put("PackageName", packageName)
        obj.put("StartTime", startTime)
        obj.put("EndTime", endTime)
        obj.put("EndHour", rawEndHour)
        obj.put("EndMinute", rawEndMinute)
        obj.put("StartHour", rawStartHour)
        obj.put("StartMinute", rawStartMinute)
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    jsonArr.put(obj)
    try {
        jsonObj.put("Loqs", jsonArr)
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    return jsonObj.toString()}

fun List<CheckBox>.hasSelection(): Boolean{
    for (item in this){
        if (item.isChecked){
            return true
        }
    }

    return false
}

fun List<ApplicationInfo>.toCustomLoqItems(): List<CustomLoqItem>{
    val list = ArrayList<CustomLoqItem>()
    for (info in this){
        val item = CustomLoqItem(info)
        list.add(item)
    }
    return list
}