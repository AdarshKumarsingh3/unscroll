package com.unscroll.app

import android.app.Application
import com.unscroll.app.service.FocusNotificationManager

class UnscrollApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Android system notification channels
        FocusNotificationManager.createNotificationChannels(this)
    }
}
