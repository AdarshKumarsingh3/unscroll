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

    // Response time tracking
    var totalInterceptions: Int
        get() = prefs?.getInt("total_interceptions", 0) ?: 0
        set(value) {
            try { prefs?.edit()?.putInt("total_interceptions", value)?.apply() } catch (_: Exception) {}
        }

    var avgResponseTimeMs: Long
        get() = prefs?.getLong("avg_response_time_ms", 0L) ?: 0L
        set(value) {
            try { prefs?.edit()?.putLong("avg_response_time_ms", value)?.apply() } catch (_: Exception) {}
        }

    var fastestResponseMs: Long
        get() = prefs?.getLong("fastest_response_ms", 0L) ?: 0L
        set(value) {
            try { prefs?.edit()?.putLong("fastest_response_ms", value)?.apply() } catch (_: Exception) {}
        }

    var bestSurferTimeSeconds: Int
        get() = prefs?.getInt("best_surfer_time_sec", 0) ?: 0
        set(value) {
            try { prefs?.edit()?.putInt("best_surfer_time_sec", value)?.apply() } catch (_: Exception) {}
        }

    var totalBreathSessions: Int
        get() = prefs?.getInt("total_breath_sessions", 0) ?: 0
        set(value) {
            try { prefs?.edit()?.putInt("total_breath_sessions", value)?.apply() } catch (_: Exception) {}
        }

    var longestFocusMinutes: Int
        get() = prefs?.getInt("longest_focus_min", 0) ?: 0
        set(value) {
            try { prefs?.edit()?.putInt("longest_focus_min", value)?.apply() } catch (_: Exception) {}
        }

    var dailyScreenTimeSetting: Float
        get() = prefs?.getFloat("daily_screen_time", 2.5f) ?: 2.5f
        set(value) {
            try { prefs?.edit()?.putFloat("daily_screen_time", value)?.apply() } catch (_: Exception) {}
        }

    fun addReclaimedTime(hours: Float) {
        val current = hoursReclaimed
        val currentUrges = urgesDefeatedCount
        hoursReclaimed = current + hours
        urgesDefeatedCount = currentUrges + 1
        
        if (streakDays == 0) {
            streakDays = 1
        }
    }

    fun recordResponseTime(responseMs: Long) {
        totalInterceptions = totalInterceptions + 1
        val currentAvg = avgResponseTimeMs
        val count = totalInterceptions
        avgResponseTimeMs = if (count <= 1) responseMs else ((currentAvg * (count - 1) + responseMs) / count)
        val fastest = fastestResponseMs
        if (fastest == 0L || responseMs < fastest) {
            fastestResponseMs = responseMs
        }
    }

    fun recordBreathSession() {
        totalBreathSessions = totalBreathSessions + 1
    }
}
