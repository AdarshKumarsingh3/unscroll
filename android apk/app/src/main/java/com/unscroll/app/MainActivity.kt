package com.unscroll.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unscroll.app.service.FocusNotificationManager
import com.unscroll.app.ui.navigation.Screen
import com.unscroll.app.ui.screens.*
import com.unscroll.app.ui.theme.*

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            FocusNotificationManager.showMindfulnessNudge(
                this,
                "Unscroll Notifications Active",
                "You will receive mindful nudges and focus session status."
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        checkAndRequestNotificationPermission()

        val navTarget = intent.getStringExtra("EXTRA_NAV_TARGET") ?: Screen.Calculator.route

        setContent {
            UnscrollTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Calculator.route

                // Navigate if triggered by interceptor service
                LaunchedEffect(navTarget) {
                    if (navTarget == "interceptor") {
                        navController.navigate(Screen.Interceptor.route) {
                            popUpTo(Screen.Calculator.route)
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BgDark,
                    bottomBar = {
                        NavigationBar(
                            containerColor = SurfaceDark,
                            tonalElevation = 8.dp
                        ) {
                            Screen.bottomNavItems.forEach { screen ->
                                val isSelected = currentRoute == screen.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            screen.icon,
                                            contentDescription = screen.title,
                                            tint = if (isSelected) TealBright else TextTertiary
                                        )
                                    },
                                    label = {
                                        Text(
                                            screen.title,
                                            color = if (isSelected) TealLight else TextTertiary,
                                            fontSize = 9.sp
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = CardDark
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Calculator.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Calculator.route) {
                            CalculatorScreen(
                                onNavigateToUrgeSurfer = { navController.navigate(Screen.UrgeSurfer.route) },
                                onNavigateToReplacements = { navController.navigate(Screen.Replacements.route) }
                            )
                        }
                        composable(Screen.Interceptor.route) {
                            InterceptorScreen(
                                onNavigateToUrgeSurfer = { navController.navigate(Screen.UrgeSurfer.route) },
                                onNavigateToReplacements = { navController.navigate(Screen.Replacements.route) }
                            )
                        }
                        composable(Screen.UrgeSurfer.route) {
                            UrgeSurferScreen(
                                onNavigateToReplacements = { navController.navigate(Screen.Replacements.route) }
                            )
                        }
                        composable(Screen.Replacements.route) {
                            ReplacementsScreen(
                                onNavigateToFocus = { navController.navigate(Screen.FocusRooms.route) }
                            )
                        }
                        composable(Screen.FocusRooms.route) {
                            FocusRoomsScreen()
                        }
                        composable(Screen.Dashboard.route) {
                            DashboardScreen()
                        }
                        composable(Screen.FounderDeck.route) {
                            FounderDeckScreen()
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
