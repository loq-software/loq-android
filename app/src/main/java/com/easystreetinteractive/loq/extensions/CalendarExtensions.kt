package com.easystreetinteractive.loq.extensions

import com.easystreetinteractive.loq.constants.Constants
import java.text.SimpleDateFormat
import java.util.*

val Calendar.dateString: String get(){

    val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return format.format(time)
}

val Calendar.timeString: String get() {
    val format = SimpleDateFormat("hh:mm a", Locale.US)
    return format.format(time)
}

val Calendar.dayOfWeekString: String get(){
    val calenderDay = get(Calendar.DAY_OF_WEEK)
    return  when(calenderDay){
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
}