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
        get() = prefs?.getInt("streak_days", 7) ?: 7
        set(value) {
            try { prefs?.edit()?.putInt("streak_days", value)?.apply() } catch (_: Exception) {}
        }

    var hoursReclaimed: Float
        get() = prefs?.getFloat("hours_reclaimed", 14.8f) ?: 14.8f
        set(value) {
            try { prefs?.edit()?.putFloat("hours_reclaimed", value)?.apply() } catch (_: Exception) {}
        }

    var urgesDefeatedCount: Int
        get() = prefs?.getInt("urges_defeated", 42) ?: 42
        set(value) {
            try { prefs?.edit()?.putInt("urges_defeated", value)?.apply() } catch (_: Exception) {}
        }

    var interceptorActive: Boolean
        get() = prefs?.getBoolean("interceptor_active", true) ?: true
        set(value) {
            try { prefs?.edit()?.putBoolean("interceptor_active", value)?.apply() } catch (_: Exception) {}
        }

    fun addReclaimedTime(hours: Float) {
        val current = hoursReclaimed
        val currentUrges = urgesDefeatedCount
        hoursReclaimed = current + hours
        urgesDefeatedCount = currentUrges + 1
    }
}
