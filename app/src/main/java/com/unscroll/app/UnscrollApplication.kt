package com.unscroll.app

import android.app.Application
import com.unscroll.app.service.FocusNotificationManager

class UnscrollApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FocusNotificationManager.createNotificationChannels(this)
        } catch (_: Exception) {
        }
    }
}
