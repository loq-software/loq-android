package com.easystreetinteractive.loq.preferences

import android.content.SharedPreferences
import com.easystreetinteractive.loq.constants.Constants

interface PreferenceManager {

    val temporaryUnlockTime: Long

    fun storeTemporaryUnlockTime()
}

class RealPreferenceManager(private val preferences: SharedPreferences): PreferenceManager{

    override val temporaryUnlockTime: Long
        get() {
            return preferences.getLong(Constants.UNLOCK_TIME_KEY, 0)
        }

    override fun storeTemporaryUnlockTime() {
       val time = System.currentTimeMillis()
        with (preferences.edit()) {
            putLong(Constants.UNLOCK_TIME_KEY, time)
            apply()
        }
    }

}