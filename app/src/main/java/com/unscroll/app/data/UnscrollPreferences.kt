package com.unscroll.app.data

import android.content.Context
import android.content.SharedPreferences

class UnscrollPreferences(context: Context) {
    private val prefs: SharedPreferences? = try {
        context.applicationContext.getSharedPreferences("unscroll_prefs", Context.MODE_PRIVATE)
    } catch (_: Exception) {
        null
    }

    var streakDays: Int
        get() = prefs?.getInt("streak_days", 0) ?: 0
        set(value) {
            try { prefs?.edit()?.putInt("streak_days", value)?.apply() } catch (_: Exception) {}
        }

    var hoursReclaimed: Float
        get() = prefs?.getFloat("hours_reclaimed", 0.0f) ?: 0.0f
        set(value) {
            try { prefs?.edit()?.putFloat("hours_reclaimed", value)?.apply() } catch (_: Exception) {}
        }

    var urgesDefeatedCount: Int
        get() = prefs?.getInt("urges_defeated", 0) ?: 0
        set(value) {
            try { prefs?.edit()?.putInt("urges_defeated", value)?.apply() } catch (_: Exception) {}
        }

    var interceptorActive: Boolean
        get() = prefs?.getBoolean("interceptor_active", true) ?: true
        set(value) {
            try { prefs?.edit()?.putBoolean("interceptor_active", value)?.apply() } catch (_: Exception) {}
        }
        
    var isDarkModeEnabled: Boolean
        get() = prefs?.getBoolean("dark_mode_enabled", true) ?: true
        set(value) {
            try { prefs?.edit()?.putBoolean("dark_mode_enabled", value)?.apply() } catch (_: Exception) {}
        }

    fun addReclaimedTime(hours: Float) {
        val current = hoursReclaimed
        val currentUrges = urgesDefeatedCount
        hoursReclaimed = current + hours
        urgesDefeatedCount = currentUrges + 1
        
        // Basic streak logic: if they used it, set streak to at least 1
        if (streakDays == 0) {
            streakDays = 1
        }
    }
}
