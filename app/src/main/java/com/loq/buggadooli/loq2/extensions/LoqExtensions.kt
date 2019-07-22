package com.loq.buggadooli.loq2.extensions

import android.widget.CheckBox
import com.loq.buggadooli.loq2.models.Loq
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

inline fun List<Loq>.isAppLocked(app: String): Boolean {
    for (loq in this) {
        if (loq.appName!!.equals(app, ignoreCase = true)
                || loq.packageName!!.equals(app, ignoreCase = true)) {
            if (isLockTime(loq))
                return true
        }
    }
    return false
}

inline fun isLockTime(loq: Loq): Boolean {
    val date = Date()   // given date
    val calendar = GregorianCalendar.getInstance() // creates a new calendar instance
    calendar.time = date   // assigns calendar to given date
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val startHour = Integer.parseInt(loq.rawStartHour!!)
    val endHour = Integer.parseInt(loq.rawEndHour!!)
    val startMintute = Integer.parseInt(loq.rawStartMinute!!)
    val endMinute = Integer.parseInt(loq.rawEndMinute!!)
    if (hour in startHour..endHour) {
        if (hour == startHour && minute < startMintute)
            return false
        return !(hour == endHour && minute > endMinute)
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