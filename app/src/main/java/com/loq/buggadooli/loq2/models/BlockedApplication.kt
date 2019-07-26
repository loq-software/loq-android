package com.loq.buggadooli.loq2.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlockedApplication(
        var id: String = "",
        val userId: String = "",
        val applicationName: String = "",
        val packageName: String = "",
        val blockBlockedDays: List<BlockedDay> = emptyList()
): Parcelable
