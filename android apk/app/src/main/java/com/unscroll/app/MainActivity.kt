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
import com.unscroll.app.ui.navigation.Screen
import com.unscroll.app.ui.screens.*
import com.unscroll.app.ui.theme.*

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Handled safely without popping immediate notification crashes
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navTarget = intent.getStringExtra("EXTRA_NAV_TARGET") ?: Screen.Calculator.route

        setContent {
            UnscrollTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Calculator.route

                // Request notification permission smoothly after the UI is rendered
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            if (!hasPermission) {
                                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } catch (_: Exception) {}
                    }
                }

                // Deep-link navigation if triggered by interceptor service
                LaunchedEffect(navTarget) {
                    if (navTarget == "interceptor") {
                        try {
                            navController.navigate(Screen.Interceptor.route) {
                                popUpTo(Screen.Calculator.route)
                            }
                        } catch (_: Exception) {}
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
                                            try {
                                                navController.navigate(screen.route) {
                                                    popUpTo(Screen.Calculator.route) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            } catch (_: Exception) {
                                                navController.navigate(screen.route)
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
}
