package com.unscroll.app

import androidx.multidex.MultiDexApplication
import com.unscroll.app.service.FocusNotificationManager

class UnscrollApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        try {
            FocusNotificationManager.createNotificationChannels(this)
        } catch (_: Exception) {
        }
    }
}
