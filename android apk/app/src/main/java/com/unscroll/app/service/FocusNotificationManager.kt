package com.unscroll.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.unscroll.app.MainActivity
import com.unscroll.app.R

object FocusNotificationManager {
    const val CHANNEL_MINDFULNESS = "channel_mindfulness"
    const val CHANNEL_FOCUS = "channel_focus"
    const val CHANNEL_STREAK = "channel_streak"

    private const val NOTIFICATION_ID_FOCUS = 1001
    private const val NOTIFICATION_ID_MINDFULNESS = 1002
    private const val NOTIFICATION_ID_STREAK = 1003

    fun createNotificationChannels(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return

                val mindfulnessChannel = NotificationChannel(
                    CHANNEL_MINDFULNESS,
                    "Mindfulness Check-Ins",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Gentle habit nudges and evening reflection reminders"
                }

                val focusChannel = NotificationChannel(
                    CHANNEL_FOCUS,
                    "Live Focus Sessions",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Ongoing timer status during deep work Pomodoro blocks"
                }

                val streakChannel = NotificationChannel(
                    CHANNEL_STREAK,
                    "Streaks & Achievements",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Milestone celebrations when you reclaim life hours"
                }

                manager.createNotificationChannels(listOf(mindfulnessChannel, focusChannel, streakChannel))
            }
        } catch (_: Exception) {
        }
    }

    fun showMindfulnessNudge(context: Context, title: String, message: String) {
        try {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_MINDFULNESS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.notify(NOTIFICATION_ID_MINDFULNESS, notification)
        } catch (_: Exception) {
        }
    }

    fun showOngoingFocusNotification(context: Context, timeFormatted: String, taskName: String) {
        try {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_FOCUS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Focus Room Active: $timeFormatted")
                .setContentText("Intention: $taskName")
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.notify(NOTIFICATION_ID_FOCUS, notification)
        } catch (_: Exception) {
        }
    }

    fun cancelFocusNotification(context: Context) {
        try {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.cancel(NOTIFICATION_ID_FOCUS)
        } catch (_: Exception) {
        }
    }

    fun showStreakCelebration(context: Context, streakDays: Int, hoursReclaimed: Float) {
        try {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_STREAK)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(" $streakDays-Day Streak Shield Unlocked!")
                .setContentText("You've reclaimed ${String.format(java.util.Locale.US, "%.1f", hoursReclaimed)} hours from algorithmic feeds.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.notify(NOTIFICATION_ID_STREAK, notification)
        } catch (_: Exception) {
        }
    }
}
