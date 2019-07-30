package com.easystreetinteractive.loq.utils

object Utils {

    fun makeLoqItemTimeString(startTime: String, endTime: String): CharSequence? {
        return "$startTime - $endTime"
    }
}
