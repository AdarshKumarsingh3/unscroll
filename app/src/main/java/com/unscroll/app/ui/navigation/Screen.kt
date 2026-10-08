package com.unscroll.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Calculator : Screen("calculator", "Cost", Icons.Default.Info)
    object Interceptor : Screen("interceptor", "Shield", Icons.Default.Lock)
    object UrgeSurfer : Screen("urge_surfer", "90s Wave", Icons.Default.PlayArrow)
    object Replacements : Screen("replacements", "Bites", Icons.Default.Star)
    object FocusRooms : Screen("focus_rooms", "Focus", Icons.Default.Notifications)
    object Dashboard : Screen("dashboard", "Saved", Icons.Default.ThumbUp)
    object FounderDeck : Screen("founder_deck", "Pitch", Icons.Default.Share)

    companion object {
        val bottomNavItems = listOf(
            Calculator,
            Interceptor,
            UrgeSurfer,
            Replacements,
            FocusRooms,
            Dashboard,
            FounderDeck
        )
    }
}
