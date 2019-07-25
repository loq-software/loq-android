package com.loq.buggadooli.loq2.extensions

import android.widget.CheckBox
import com.loq.buggadooli.loq2.models.BlockTime
import com.loq.buggadooli.loq2.models.BlockedDay

fun List<CheckBox>.dayCheckBoxesToBlockedDays(time: BlockTime): List<BlockedDay>{
    val days = ArrayList<BlockedDay>()
    for (checkbox in this){
        if (checkbox.isChecked){
            val blockedDay = BlockedDay(checkbox.text.toString(), time)
            days.add(blockedDay)
        }
    }
    return days
}