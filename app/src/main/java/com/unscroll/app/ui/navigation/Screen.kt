package com.unscroll.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Calculator : Screen("calculator", "Cost", Icons.Default.Calculate)
    object Interceptor : Screen("interceptor", "Shield", Icons.Default.Shield)
    object UrgeSurfer : Screen("urge_surfer", "90s Wave", Icons.Default.Waves)
    object Replacements : Screen("replacements", "Bites", Icons.Default.Psychology)
    object FocusRooms : Screen("focus_rooms", "Focus", Icons.Default.HourglassTop)
    object Dashboard : Screen("dashboard", "Saved", Icons.Default.Insights)
    object FounderDeck : Screen("founder_deck", "Pitch", Icons.Default.Work)

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
