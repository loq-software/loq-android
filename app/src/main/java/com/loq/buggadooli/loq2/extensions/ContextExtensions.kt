package com.loq.buggadooli.loq2.extensions

import android.content.Context

@Suppress("UNCHECKED_CAST")
fun <T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}