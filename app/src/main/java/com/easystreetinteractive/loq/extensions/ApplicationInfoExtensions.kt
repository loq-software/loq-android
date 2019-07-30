package com.easystreetinteractive.loq.extensions

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.easystreetinteractive.loq.models.CustomLoqItem

fun List<CustomLoqItem>.getSelectedApplicationInformationList(): List<ApplicationInfo>{
    val list = ArrayList<ApplicationInfo>()

    for (item in this){
        if (item.isSelected){
            list.add(item.info)
        }
    }
    return list
}

fun ApplicationInfo.getAppName(manager: PackageManager): String{
    return this.loadLabel(manager).toString()
}