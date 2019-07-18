package com.loq.buggadooli.loq2.ui.bindings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.loq.buggadooli.loq2.utils.Utils

@BindingAdapter("loqItemStartTime", "loqItemEndTime")
fun setLoqItemTime(textView: TextView,
                   startTime: String?,
                   endTime: String?){
    if (startTime == null || endTime == null) return
    textView.text = Utils.makeLoqItemTimeString(startTime, endTime)
}