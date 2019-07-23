package com.loq.buggadooli.loq2.models

import android.content.pm.ApplicationInfo

data class CustomLoqItem(val info: ApplicationInfo, var isSelected: Boolean = false)