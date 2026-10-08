package com.unscroll.app.data

import android.content.Context
import android.content.SharedPreferences

class UnscrollPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("unscroll_prefs", Context.MODE_PRIVATE)

    var streakDays: Int
        get() = prefs.getInt("streak_days", 7)
        set(value) = prefs.edit().putInt("streak_days", value).apply()

    var hoursReclaimed: Float
        get() = prefs.getFloat("hours_reclaimed", 14.8f)
        set(value) = prefs.edit().putFloat("hours_reclaimed", value).apply()

    var urgesDefeatedCount: Int
        get() = prefs.getInt("urges_defeated", 42)
        set(value) = prefs.edit().putInt("urges_defeated", value).apply()

    var interceptorActive: Boolean
        get() = prefs.getBoolean("interceptor_active", true)
        set(value) = prefs.edit().putBoolean("interceptor_active", value).apply()

    fun addReclaimedTime(hours: Float) {
        hoursReclaimed += hours
        urgesDefeatedCount += 1
    }
}
