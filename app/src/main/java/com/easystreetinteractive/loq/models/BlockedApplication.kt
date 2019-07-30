package com.easystreetinteractive.loq.models

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
