package com.easystreetinteractive.loq.ui.bindings

import android.content.pm.ApplicationInfo
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.easystreetinteractive.loq.extensions.dateString
import com.easystreetinteractive.loq.models.BlockTime
import com.easystreetinteractive.loq.models.BlockedDay
import com.easystreetinteractive.loq.utils.Utils
import java.util.*
import android.widget.ArrayAdapter
import android.widget.Button
import com.easystreetinteractive.loq.extensions.getAppName
import com.easystreetinteractive.loq.extensions.timeString
import com.easystreetinteractive.loq.utils.Event


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

@BindingAdapter("date")
fun setDate(view: TextView, event: Event<Calendar>?){
    val calender = event?.getContentIfNotHandled()?: return
    val string = calender.dateString
    view.text = string

}

@BindingAdapter("time")
fun setTime(view: Button, event: Event<Calendar>?){
    val calendar = event?.getContentIfNotHandled()?: return
    val string = calendar.timeString
    view.text = string
}

@BindingAdapter("applicationName")
fun setApplicationName(view: TextView, event: Event<ApplicationInfo>?){
    val name = event?.getContentIfNotHandled()?.getAppName(view.context.packageManager)?: "Application Name"
    view.text = name
}