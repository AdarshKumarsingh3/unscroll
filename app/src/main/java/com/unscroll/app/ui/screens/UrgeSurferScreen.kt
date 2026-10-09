package com.unscroll.app.ui.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.R
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.FocusNotificationManager
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun UrgeSurferScreen(
    onNavigateToReplacements: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }

    var isRunning by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableIntStateOf(90) }
    var breathPhase by remember { mutableStateOf("Inhale") }
    var phaseSeconds by remember { mutableIntStateOf(4) }
    var tapsCount by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var initialCraving by remember { mutableFloatStateOf(8f) }

    // Smooth Breathing Animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val orbColor by animateColorAsState(
        targetValue = when (breathPhase) {
            "Inhale" -> TealPrimary
            "Hold" -> BlueCalm
            "Exhale" -> EmeraldAccent
            else -> TealPrimary
        },
        animationSpec = tween(1000), label = "orbColor"
    )

    LaunchedEffect(isRunning, secondsRemaining) {
        if (isRunning && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--

            if (phaseSeconds > 1) {
                phaseSeconds--
            } else {
                phaseSeconds = 4
                breathPhase = when (breathPhase) {
                    "Inhale" -> "Hold"
                    "Hold" -> "Exhale"
                    "Exhale" -> "Inhale"
                    else -> "Inhale"
                }
                SoundSynthesizer.playSingingBowlChime(432f, 1.2f)
            }

            if (secondsRemaining == 0) {
                isRunning = false
                isFinished = true
                prefs.addReclaimedTime(0.25f)
                HapticFeedback.triggerSuccess(context)
                SoundSynthesizer.playSingingBowlChime(528f, 3.5f)
                FocusNotificationManager.showStreakCelebration(context, prefs.streakDays, prefs.hoursReclaimed)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(BlueCalm.copy(alpha = 0.15f))
                .border(1.dp, BlueCalm.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "🧠 Craving Wave Protocol • 90s",
                color = BlueCalm,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Ride the Urge",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Dopamine cravings peak for 60-90s. Ride the wave instead of scrolling. Watch it collapse.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Main Breathing Stage Card
        Card(
            modifier = Modifier.fillMaxWidth().height(360.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (!isFinished) {
                    // Tactile Breathing Orb
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .scale(if (isRunning) breathScale else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        orbColor.copy(alpha = 0.3f),
                                        orbColor.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(3.dp, orbColor.copy(alpha = 0.5f), CircleShape)
                            .clickable {
                                if (isRunning) {
                                    tapsCount++
                                    HapticFeedback.triggerClick(context)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isRunning) {
                                Text(
                                    text = breathPhase.uppercase(),
                                    color = orbColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "s",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 56.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "👆 Tap to anchor ()",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_nav_urge),
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Ready to Surf", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("Tap Start Below", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            }
                        }
                    }

                    // Progress Bar at Bottom
                    LinearProgressIndicator(
                        progress = ((90f - secondsRemaining) / 90f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .align(Alignment.BottomCenter),
                        color = orbColor,
                        trackColor = Color.Transparent
                    )
                } else {
                    // Completed Celebration
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(EmeraldAccent.copy(alpha=0.2f))
                                .border(2.dp, EmeraldAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav_interceptor),
                                contentDescription = null,
                                tint = EmeraldAccent,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        
                        Text(
                            text = "Craving Peak Collapsed!",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "You rode the neurochemical surge without reacting. Your receptors just recalibrated.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 24.sp
                        )
                        Button(
                            onClick = onNavigateToReplacements,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent)
                        ) {
                            Text("Explore 30s Dopamine Bites", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        if (!isFinished) {
            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (!isRunning) {
                            isRunning = true
                            secondsRemaining = 90
                            tapsCount = 0
                            HapticFeedback.triggerClick(context)
                            SoundSynthesizer.playSingingBowlChime(432f, 2.5f)
                        } else {
                            isRunning = false
                            HapticFeedback.triggerClick(context)
                        }
                    },
                    modifier = Modifier.weight(1f).height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(painter = painterResource(R.drawable.ic_nav_urge), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(if (isRunning) "Pause Wave" else "Start 90s Surfer", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                }

                if (isRunning) {
                    IconButton(
                        onClick = {
                            isRunning = false
                            secondsRemaining = 90
                            HapticFeedback.triggerClick(context)
                        },
                        modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_nav_focus), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}
