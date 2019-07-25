package com.loq.buggadooli.loq2.utils

import com.loq.buggadooli.loq2.models.BlockTime
import com.loq.buggadooli.loq2.models.BlockedDay

object BlockedDayUtilities {
    fun buildDay(
            dayOfWeek: String,
            startHour: Int = -1,
            endHour: Int = -1,
            startMinute: Int = -1,
            endMinute: Int = -1
    ): BlockedDay {

        val time = BlockTime(startHour, startMinute, endHour, endMinute)
        return BlockedDay(dayOfWeek, time)
    }
}