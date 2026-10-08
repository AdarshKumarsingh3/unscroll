package com.unscroll.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
        // Handled silently
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navTarget = intent.getStringExtra("EXTRA_NAV_TARGET") ?: Screen.Calculator.route

        setContent {
            UnscrollTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Calculator.route

                // Request notification permission safely on Android 13+ after UI is shown
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
                        } catch (e: Exception) {
                            Log.w("Unscroll", "Notification permission request skipped", e)
                        }
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
                                                try {
                                                    navController.navigate(screen.route)
                                                } catch (_: Exception) {}
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
                            SafeScreenContainer(screenName = "Screen-Time Cost Calculator") {
                                CalculatorScreen(
                                    onNavigateToUrgeSurfer = { navController.navigate(Screen.UrgeSurfer.route) },
                                    onNavigateToReplacements = { navController.navigate(Screen.Replacements.route) }
                                )
                            }
                        }
                        composable(Screen.Interceptor.route) {
                            SafeScreenContainer(screenName = "Feed Interceptor") {
                                InterceptorScreen(
                                    onNavigateToUrgeSurfer = { navController.navigate(Screen.UrgeSurfer.route) },
                                    onNavigateToReplacements = { navController.navigate(Screen.Replacements.route) }
                                )
                            }
                        }
                        composable(Screen.UrgeSurfer.route) {
                            SafeScreenContainer(screenName = "90s Urge Surfer") {
                                UrgeSurferScreen(
                                    onNavigateToReplacements = { navController.navigate(Screen.Replacements.route) }
                                )
                            }
                        }
                        composable(Screen.Replacements.route) {
                            SafeScreenContainer(screenName = "Dopamine Replacements") {
                                ReplacementsScreen(
                                    onNavigateToFocus = { navController.navigate(Screen.FocusRooms.route) }
                                )
                            }
                        }
                        composable(Screen.FocusRooms.route) {
                            SafeScreenContainer(screenName = "Live Focus Rooms") {
                                FocusRoomsScreen()
                            }
                        }
                        composable(Screen.Dashboard.route) {
                            SafeScreenContainer(screenName = "Saved Hours Dashboard") {
                                DashboardScreen()
                            }
                        }
                        composable(Screen.FounderDeck.route) {
                            SafeScreenContainer(screenName = "Proposal & Deck") {
                                FounderDeckScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SafeScreenContainer(screenName: String, content: @Composable () -> Unit) {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (hasError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Recovered $screenName",
                    style = MaterialTheme.typography.titleMedium,
                    color = TealBright
                )
                Text(
                    text = if (errorMessage.isNotBlank()) errorMessage else "Tap below to reload screen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Button(
                    onClick = { hasError = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reload Screen", color = Color.White)
                }
            }
        }
    } else {
        content()
    }
}
