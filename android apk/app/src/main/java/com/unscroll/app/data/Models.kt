package com.unscroll.app.data

enum class TriggerCategory(val displayName: String, val emoji: String) {
    BORED("Bored", ""),
    STRESSED("Stressed / Anxious", ""),
    RESTLESS("Restless / Sluggish", ""),
    LONELY("Lonely", ""),
    PRODUCTIVE("Want to Produce", ""),
    LEARN("Want to Learn", "")
}

data class ReplacementActivity(
    val id: String,
    val title: String,
    val category: TriggerCategory,
    val durationText: String,
    val description: String,
    val actionType: String
)

data class FocusPeer(
    val id: String,
    val name: String,
    val flag: String,
    val task: String,
    val minutesActive: Int
)

data class AppInterceptTarget(
    val packageName: String,
    val appName: String,
    val isMonitored: Boolean
)

