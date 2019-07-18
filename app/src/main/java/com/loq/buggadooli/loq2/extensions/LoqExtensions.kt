package com.loq.buggadooli.loq2.extensions

import com.loq.buggadooli.loq2.models.Loq
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