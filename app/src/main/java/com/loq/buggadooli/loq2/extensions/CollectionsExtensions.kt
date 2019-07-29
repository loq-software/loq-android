package com.loq.buggadooli.loq2.extensions

fun IntArray.asString(): String {
    return joinToString(separator = ", ", prefix = "[", postfix = "]")
}

fun LongArray.asString(): String {
    return joinToString(separator = ", ", prefix = "[", postfix = "]")
}

fun <T> Array<T>.asString(): String {
    return joinToString(separator = ", ", prefix = "[", postfix = "]")
}
