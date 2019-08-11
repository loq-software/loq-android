package com.easystreetinteractive.loq.ui.bindings

import android.content.pm.ApplicationInfo
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.easystreetinteractive.loq.models.BlockTime
import com.easystreetinteractive.loq.models.BlockedDay
import com.easystreetinteractive.loq.utils.Utils

@BindingAdapter("loqTimes")
fun setLoqItemTime(textView: TextView,
                   list: List<BlockedDay>){
    var times = ""
    for (day in list) {
        val time = day.time?: continue
        times += "${Utils.makeLoqItemTimeString(time)}\n"
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
        days += "${day.dayOfWeek}\n"
    }
    view.text = days.trim()
}

@BindingAdapter("loqTime")
fun setDayIntervalString(view: TextView, time: BlockTime){
    view.text = "${Utils.makeLoqItemTimeString(time)}"
}