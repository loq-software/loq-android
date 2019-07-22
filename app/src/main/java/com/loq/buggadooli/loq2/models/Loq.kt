package com.loq.buggadooli.loq2.models

data class Loq(
        @kotlin.jvm.JvmField
        var appName: String? = null,
        @kotlin.jvm.JvmField var days: List<String>? = null,
        @kotlin.jvm.JvmField var daysStr: String? = null,
        @kotlin.jvm.JvmField var startTime: String? = null,
        @kotlin.jvm.JvmField var endTime: String? = null,
        @kotlin.jvm.JvmField var packageName: String? = null,
        @kotlin.jvm.JvmField var rawStartHour: String? = null,
        @kotlin.jvm.JvmField var rawStartMinute: String? = null,
        @kotlin.jvm.JvmField var rawEndHour: String? = null,
        @kotlin.jvm.JvmField var rawEndMinute: String? = null
) {
}
