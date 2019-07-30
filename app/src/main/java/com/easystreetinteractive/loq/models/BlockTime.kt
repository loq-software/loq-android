package com.easystreetinteractive.loq.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlockTime(val startHour: Int = -1,
                     val startMinute: Int = -1,
                     val endHour: Int = -1,
                     val endMinute: Int = -1): Parcelable