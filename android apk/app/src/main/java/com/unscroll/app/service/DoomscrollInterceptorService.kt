package com.unscroll.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import com.unscroll.app.MainActivity
import com.unscroll.app.data.UnscrollPreferences

class DoomscrollInterceptorService : AccessibilityService() {

    private lateinit var preferences: UnscrollPreferences
    private var lastInterceptTimestamp: Long = 0

    // Monitored packages for short-form feed traps
    private val monitoredPackages = setOf(
        "com.instagram.android",
        "com.zhiliaoapp.musically",
        "com.ss.android.ugc.trill",
        "com.google.android.youtube",
        "com.twitter.android",
        "com.reddit.frontpage",
        "com.facebook.katana"
    )

    override fun onCreate() {
        super.onCreate()
        try {
            preferences = UnscrollPreferences(this)
        } catch (_: Exception) {
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        try {
            if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
            if (!::preferences.isInitialized || !preferences.interceptorActive) return

            val packageName = event.packageName?.toString() ?: return

            if (monitoredPackages.contains(packageName)) {
                val now = SystemClock.elapsedRealtime()
                // Enforce a 60-second cooldown so user isn't stuck in loop if they choose to continue
                if (now - lastInterceptTimestamp > 60_000) {
                    lastInterceptTimestamp = now
                    launchPauseScreen(packageName)
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun launchPauseScreen(targetPackage: String) {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_INTERCEPTED_PACKAGE", targetPackage)
                putExtra("EXTRA_NAV_TARGET", "interceptor")
            }
            startActivity(intent)

            FocusNotificationManager.showMindfulnessNudge(
                this,
                "Pause. Is this conscious?",
                "Unscroll paused $targetPackage. Take 1 deep breath before you proceed."
            )
        } catch (_: Exception) {
        }
    }

    override fun onInterrupt() {
    }
}
