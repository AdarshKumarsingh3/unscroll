package com.unscroll.app.ui.navigation

import androidx.annotation.DrawableRes
import com.unscroll.app.R

sealed class Screen(val route: String, val title: String, @DrawableRes val iconRes: Int) {
    object Calculator : Screen("calculator", "Cost", R.drawable.ic_nav_calculator)
    object Interceptor : Screen("interceptor", "Shield", R.drawable.ic_nav_interceptor)
    object UrgeSurfer : Screen("urge_surfer", "90s Wave", R.drawable.ic_nav_urge)
    object Replacements : Screen("replacements", "Bites", R.drawable.ic_nav_replacements)
    object FocusRooms : Screen("focus_rooms", "Focus", R.drawable.ic_nav_focus)
    object Dashboard : Screen("dashboard", "Saved", R.drawable.ic_nav_dashboard)
    object FounderDeck : Screen("founder_deck", "Pitch", R.drawable.ic_nav_pitch)
    object MindfulTap : Screen("mindful_tap", "Zen Tap", R.drawable.ic_nav_focus)

    companion object {
        val bottomNavItems get() = listOf(
            Dashboard,
            Calculator,
            Interceptor,
            UrgeSurfer,
            Replacements
        )
    }
}
