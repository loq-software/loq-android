package com.easystreetinteractive.loq.extensions

import android.widget.CheckBox
import com.easystreetinteractive.loq.models.BlockTime
import com.easystreetinteractive.loq.models.BlockedDay

fun List<CheckBox>.dayCheckBoxesToBlockedDays(time: BlockTime): MutableList<BlockedDay>{
    val days = ArrayList<BlockedDay>()
    for (checkbox in this){
        if (checkbox.isChecked){
            val blockedDay = BlockedDay(checkbox.text.toString(), time)
            days.add(blockedDay)
        }
    }
    return days
}