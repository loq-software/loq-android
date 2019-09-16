package com.easystreetinteractive.loq.extensions

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.easystreetinteractive.loq.R

private var toast: Toast? = null

fun Context?.toast(message: String) {
    if (this == null) {
        return
    }
    toast?.cancel()
    toast = Toast.makeText(this, message, LENGTH_SHORT)
            .apply {
                show()
            }
}

@Suppress("UNCHECKED_CAST")
fun <T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

fun Context.getPrivatePreferences(): SharedPreferences {
    return this.getSharedPreferences(this.getString(R.string.private_preference_key), Context.MODE_PRIVATE)
}