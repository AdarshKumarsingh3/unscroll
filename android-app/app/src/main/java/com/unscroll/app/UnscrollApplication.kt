package com.unscroll.app

import android.app.Application
import android.util.Log
import com.unscroll.app.service.FocusNotificationManager

class UnscrollApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set global uncaught exception guard to capture any background thread crashes
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UnscrollApp", "Uncaught exception in thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        try {
            FocusNotificationManager.createNotificationChannels(this)
        } catch (e: Exception) {
            Log.w("UnscrollApp", "Notification channels creation warning", e)
        }
    }
}
