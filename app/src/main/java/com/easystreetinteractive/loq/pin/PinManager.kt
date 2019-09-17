package com.easystreetinteractive.loq.pin

import android.content.Context
import com.easystreetinteractive.loq.constants.Constants
import com.easystreetinteractive.loq.extensions.getPrivatePreferences

interface PinManager{

    fun storePin(pin: String)

    fun removePin()

    fun hasStoredPin(): Boolean

    fun validatePin(pin: String): Boolean
}

class RealPinManager(private val context: Context): PinManager {

    override fun storePin(pin: String) {
        context.getPrivatePreferences()
                .edit()
                .putString(Constants.PIN, pin.trim())
                .apply()
    }

    override fun removePin() {
        context.getPrivatePreferences()
                .edit()
                .putString(Constants.PIN, null)
                .apply()
    }

    override fun hasStoredPin(): Boolean {
        val pin = context.getPrivatePreferences().getString(Constants.PIN, null) ?: return false
        return pin.trim().isNotBlank()
    }

    override fun validatePin(pin: String): Boolean {
        val storedPin = context.getPrivatePreferences().getString(Constants.PIN, null)?: return false
        return pin.isNotBlank() && storedPin.contentEquals(pin)
    }
}