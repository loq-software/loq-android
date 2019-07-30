package com.easystreetinteractive.loq.extensions

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

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