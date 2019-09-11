package com.easystreetinteractive.loq.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlockedDay(
        val dayOfWeek: String = "",// todo: Use integer for day of week instead of String
        val dayOfMonth: Int = -1,
        val monthOfYear: Int = -1,
        var time: BlockTime? = null
): Parcelable