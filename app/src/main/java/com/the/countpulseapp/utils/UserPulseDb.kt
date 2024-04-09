package com.the.countpulseapp.utils

import android.content.SharedPreferences
import com.the.countpulseapp.data.Constants
import com.the.countpulseapp.data.Constants.USER_SENSOR_USE

class UserPulseDb(private val preferences: SharedPreferences) {

    fun userUseSensor(value: String) {
        preferences.edit().putString(USER_SENSOR_USE, value).apply()
    }
    fun whatStateWithSensor():String {
        return preferences.getString(USER_SENSOR_USE, Constants.USER_SENSOR_DEF)!!
    }
}