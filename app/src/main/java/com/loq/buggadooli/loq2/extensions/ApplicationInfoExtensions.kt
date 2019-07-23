package com.loq.buggadooli.loq2.extensions

import android.content.pm.ApplicationInfo
import com.loq.buggadooli.loq2.models.CustomLoqItem

fun List<CustomLoqItem>.getSelectedApplicationInformationList(): List<ApplicationInfo>{
    val list = ArrayList<ApplicationInfo>()

    for (item in this){
        if (item.isSelected){
            list.add(item.info)
        }
    }
    return list
}