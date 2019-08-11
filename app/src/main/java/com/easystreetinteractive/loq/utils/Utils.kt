package com.easystreetinteractive.loq.utils

import com.easystreetinteractive.loq.models.BlockTime

object Utils {

    fun makeLoqItemTimeString(blockTime: BlockTime): CharSequence? {

        val startHour = blockTime.startHour
        val startMinute = blockTime.startMinute

        val endHour = blockTime.endHour
        val endMinute = blockTime.endMinute

        var startTimeOfDay = "AM"
        var endTimeOfDay = "AM"

        if (startHour > 11) {
            startTimeOfDay = "PM"
        }
        if (endHour > 11){
            endTimeOfDay = "PM"
        }

        val startHourStandard = getStandardTimeHourFrom24HourTime(startHour)
        val endHourStandard = getStandardTimeHourFrom24HourTime(endHour)

        return "$startHourStandard:${getMinuteString(startMinute)}$startTimeOfDay - $endHourStandard:${getMinuteString(endMinute)}$endTimeOfDay"
    }

    private fun getStandardTimeHourFrom24HourTime(hour: Int): Int{
        var standardHour = hour
        if (hour > 12) {
            standardHour -= 12
        }
        else if(hour == 0){
            standardHour = 12
        }
        return standardHour
    }

    private fun getMinuteString(minute: Int): String{
        return if(minute < 10) "0$minute" else minute.toString()

    }
}
