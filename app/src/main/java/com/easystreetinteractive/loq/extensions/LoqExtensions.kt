package com.easystreetinteractive.loq.extensions

import android.content.pm.ApplicationInfo
import android.widget.CheckBox
import com.easystreetinteractive.loq.constants.Constants
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.models.CustomLoqItem
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
            val startMinute = day.time?.startMinute ?: 0
            val endMinute = day.time?.endMinute ?: 0

            if (startHour > endHour){
                if (hour == endHour){
                    if (minute >= startMinute && minute <= endMinute) {
                        return true
                    }
                }
                else if (hour < endHour){
                   return true
                }
            }
            else{
                if (hour == startHour && hour < endHour){
                    if (minute >= startMinute){
                        return true
                    }
                }
                else if(hour == startHour && hour == endHour){
                    if (minute >= startMinute && hour <= endMinute){
                        return true
                    }
                }
                else if (hour == endHour && hour > startHour){
                    if (minute <= endMinute){
                        return true
                    }
                }
                else if (hour > startHour && hour < endHour){
                    return true
                }
              /*  if (hour >= startHour && hour <= endHour){
                    if (minute >= startMinute && minute <= endMinute) {
                        return true
                    }
                }*/
            }
        }
    }
    return false
}

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