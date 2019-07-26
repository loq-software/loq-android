package com.loq.buggadooli.loq2.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlockedDay(val dayOfWeek: String = "", var time: BlockTime? = null): Parcelable