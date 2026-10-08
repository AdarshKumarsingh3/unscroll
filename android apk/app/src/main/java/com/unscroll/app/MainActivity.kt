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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
        // Silently handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val navTarget = try {
                intent?.getStringExtra("EXTRA_NAV_TARGET") ?: Screen.Calculator.route
            } catch (_: Exception) {
                Screen.Calculator.route
            }

            setContent {
                UnscrollTheme {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Calculator.route

                    // Safe notification permission request on Android 13+
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
                                Log.w("Unscroll", "Permission launcher skipped", e)
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
                        topBar = {
                            // Top quick navigation for Focus Rooms & Pitch
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceDark)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_notification),
                                        contentDescription = null,
                                        tint = TealBright,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "UNSCROLL",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                navController.navigate(Screen.FocusRooms.route)
                                            } catch (_: Exception) {}
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (currentRoute == Screen.FocusRooms.route) CardDark else Color.Transparent
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (currentRoute == Screen.FocusRooms.route) TealBright else BorderDark)
                                    ) {
                                        Text("Focus", color = if (currentRoute == Screen.FocusRooms.route) TealLight else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                navController.navigate(Screen.FounderDeck.route)
                                            } catch (_: Exception) {}
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (currentRoute == Screen.FounderDeck.route) CardDark else Color.Transparent
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (currentRoute == Screen.FounderDeck.route) AmberAccent else BorderDark)
                                    ) {
                                        Text("Pitch", color = if (currentRoute == Screen.FounderDeck.route) AmberAccent else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        },
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
                                                painter = painterResource(screen.iconRes),
                                                contentDescription = screen.title,
                                                tint = if (isSelected) TealBright else TextTertiary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                screen.title,
                                                color = if (isSelected) TealLight else TextTertiary,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
        } catch (e: Throwable) {
            Log.e("MainActivity", "Fatal initialization prevented", e)
            setContent {
                UnscrollTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = BgDark
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Unscroll Initialized", color = TealBright, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Notice: ${e.localizedMessage ?: "Safe Mode Active"}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { recreate() },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Text("Restart Application", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
