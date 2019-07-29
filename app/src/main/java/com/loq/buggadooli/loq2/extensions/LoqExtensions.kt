package com.loq.buggadooli.loq2.extensions

import android.content.pm.ApplicationInfo
import android.widget.CheckBox
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.models.BlockedApplication
import com.loq.buggadooli.loq2.models.CustomLoqItem
import java.text.SimpleDateFormat
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

            val startString = "$startHour:$startMintute:00"
            val time1 = SimpleDateFormat("HH:mm:ss").parse(startString)
            val startCalendar = Calendar.getInstance()
            startCalendar.time = time1

            val endString = "$endHour:$endMinute:00"
            val time2 = SimpleDateFormat("HH:mm:ss").parse(endString)
            val endCalendar = Calendar.getInstance()
            endCalendar.time = time2

            val currentTimsString = "$hour:$minute:00"
            val d = SimpleDateFormat("HH:mm:ss").parse(currentTimsString)
            val calendar3 = Calendar.getInstance()
            calendar3.time = d

         /*   if (hour in startHour..endHour) {
                if (hour == startHour && minute < startMintute)
                    continue
                if (hour == endHour && minute > endMinute)
                    continue
                return true
            }*/

            if (time2.compareTo(d) < 0)
            {
                endCalendar.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            val actualTime = calendar3.getTime();
            if ((d.after(startCalendar.getTime()) ||
                            actualTime.compareTo(startCalendar.getTime()) == 0) &&
                    actualTime.before(endCalendar.getTime()))
            {
                return true
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