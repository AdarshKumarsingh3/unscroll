package com.unscroll.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.FocusNotificationManager
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.navigation.Screen
import com.unscroll.app.ui.screens.*
import com.unscroll.app.ui.theme.UnscrollTheme
import com.unscroll.app.ui.theme.TealPrimary
import com.unscroll.app.ui.theme.TealBright
import com.unscroll.app.ui.theme.PurpleAccent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val prefs = remember { UnscrollPreferences(context) }
            
            var isDarkTheme by remember { mutableStateOf(prefs.isDarkModeEnabled) }
            
            UnscrollTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UnscrollApp(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { 
                            isDarkTheme = !isDarkTheme 
                            prefs.isDarkModeEnabled = isDarkTheme
                            HapticFeedback.triggerClick(context)
                            SoundSynthesizer.playTick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UnscrollApp(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current

    val hideBottomBarRoutes = listOf(
        Screen.UrgeSurfer.route,
        Screen.FocusRooms.route,
        Screen.FounderDeck.route,
        Screen.MindfulTap.route
    )
    val showBottomBar = currentDestination?.route !in hideBottomBarRoutes

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_nav_interceptor),
                        contentDescription = "Logo",
                        tint = TealPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UNSCROLL",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 2.sp
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Theme toggle
                    IconButton(onClick = onThemeToggle, modifier = Modifier.size(40.dp)) {
                        Text(if (isDarkTheme) "\u2600\uFE0F" else "\uD83C\uDF19", fontSize = 18.sp)
                    }
                    // Zen Tap game
                    IconButton(
                        onClick = {
                            HapticFeedback.triggerClick(context)
                            SoundSynthesizer.playWhoosh()
                            navController.navigate(Screen.MindfulTap.route)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text("\uD83C\uDFAE", fontSize = 18.sp)
                    }
                    // Focus rooms
                    IconButton(
                        onClick = {
                            HapticFeedback.triggerClick(context)
                            navController.navigate(Screen.FocusRooms.route)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_nav_focus), contentDescription = "Focus", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
                    }
                    // Founder deck
                    IconButton(
                        onClick = {
                            HapticFeedback.triggerClick(context)
                            navController.navigate(Screen.FounderDeck.route)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_nav_pitch), contentDescription = "Pitch", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 0.dp
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(screen.iconRes),
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealBright,
                                selectedTextColor = TealBright,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = TealPrimary.copy(alpha = 0.15f)
                            ),
                            onClick = {
                                HapticFeedback.triggerClick(context)
                                SoundSynthesizer.playTick()
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(280)) + fadeIn(tween(280))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(280)) + fadeOut(tween(200))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(280)) + fadeIn(tween(280))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(280)) + fadeOut(tween(200))
            }
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
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
            composable(Screen.Replacements.route) { ReplacementsScreen() }
            composable(Screen.UrgeSurfer.route) { 
                UrgeSurferScreen(
                    onNavigateToReplacements = { 
                        navController.popBackStack()
                        navController.navigate(Screen.Replacements.route) 
                    }
                ) 
            }
            composable(Screen.FocusRooms.route) { 
                FocusRoomsScreen(onNavigateBack = { navController.popBackStack() }) 
            }
            composable(Screen.FounderDeck.route) { 
                FounderDeckScreen(onNavigateBack = { navController.popBackStack() }) 
            }
            composable(Screen.MindfulTap.route) {
                MindfulTapScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
