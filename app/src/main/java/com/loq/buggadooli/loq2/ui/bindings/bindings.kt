package com.loq.buggadooli.loq2.ui.bindings

import android.content.pm.ApplicationInfo
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.loq.buggadooli.loq2.models.BlockedDay
import com.loq.buggadooli.loq2.utils.Utils

@BindingAdapter("loqTimes")
fun setLoqItemTime(textView: TextView,
                   list: List<BlockedDay>){
    var times = ""
    for (day in list) {
        val time = day.time?: continue
        val startTime = "${time.startHour}:${time.startMinute}"
        val endTime = "${time.endHour}:${time.endMinute}"
        val timeString = Utils.makeLoqItemTimeString(startTime, endTime)
        times += "$timeString "
    }
    textView.text = times.trim()
}

@BindingAdapter("label")
fun loadLabel(view: TextView, info: ApplicationInfo){
    view.text = info.loadLabel(view.context.packageManager)
}

@BindingAdapter("daysString")
fun loadDayString(view: TextView, list: List<BlockedDay>){
    var days = ""
    for (day in list) {
        days += "${day.dayOfWeek} "
    }
    view.text = days.trim()
}