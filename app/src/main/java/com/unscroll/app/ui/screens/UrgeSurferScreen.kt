package com.unscroll.app.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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

    // Breathing Animation Scale
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Timer Effect
    LaunchedEffect(isRunning, secondsRemaining) {
        if (isRunning && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--

            // Update 4s phase
            if (phaseSeconds > 1) {
                phaseSeconds--
            } else {
                phaseSeconds = 4
                breathPhase = when (breathPhase) {
                    "Inhale" -> "Hold"
                    "Hold" -> "Exhale"
                    "Exhale" -> "Rest"
                    else -> "Inhale"
                }
                SoundSynthesizer.playSingingBowlChime(432f, 1.2f)
            }

            if (secondsRemaining == 0) {
                isRunning = false
                isFinished = true
                prefs.addReclaimedTime(0.25f) // ~15 mins saved
                SoundSynthesizer.playSingingBowlChime(528f, 3.5f)
                FocusNotificationManager.showStreakCelebration(context, prefs.streakDays, prefs.hoursReclaimed)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(TealPrimary.copy(alpha = 0.15f))
                .border(1.dp, TealPrimary.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "🌊 Dr. Marlatt's Craving Wave Protocol • 90 Seconds",
                color = TealLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "The 90-Second Urge Surfer",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            text = "Dopamine cravings peak for 60-90s, then naturally collapse. Ride the wave instead of scrolling.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Main Breathing Stage Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (!isFinished) {
                    // Tactile Breathing Orb
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .scale(if (isRunning) breathScale else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        TealBright.copy(alpha = 0.35f),
                                        TealPrimary.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(2.dp, TealBright.copy(alpha = 0.6f), CircleShape)
                            .clickable {
                                if (isRunning) {
                                    tapsCount++
                                    triggerPhoneHaptic(context)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isRunning) {
                                Text(
                                    text = breathPhase.uppercase(),
                                    color = TealLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${secondsRemaining}s",
                                    color = Color.White,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "👆 Tap circle ($tapsCount)",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_nav_urge),
                                    contentDescription = null,
                                    tint = TealLight,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Ready to Surf", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("90s neuro reset", color = TextTertiary, fontSize = 11.sp)
                            }
                        }
                    }

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = ((90f - secondsRemaining) / 90f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = TealBright,
                        trackColor = BorderDark
                    )

                    // Controls
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                if (!isRunning) {
                                    isRunning = true
                                    secondsRemaining = 90
                                    tapsCount = 0
                                    SoundSynthesizer.playSingingBowlChime(432f, 2.5f)
                                } else {
                                    isRunning = false
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Icon(painter = painterResource(R.drawable.ic_nav_urge), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRunning) "Pause Wave" else "Start 90s Surfer", fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = {
                                isRunning = false
                                secondsRemaining = 90
                            }
                        ) {
                            Icon(painter = painterResource(R.drawable.ic_nav_focus), contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                } else {
                    // Completed Celebration
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_nav_interceptor),
                            contentDescription = null,
                            tint = EmeraldAccent,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Craving Peak Collapsed!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "You successfully rode through the neurochemical surge without reacting. Your receptors just recalibrated.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onNavigateToReplacements,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealBright)
                        ) {
                            Text("Explore 30s Dopamine Bites", color = BgDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Craving Slider Gauge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pre-Surfer Craving Intensity", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("${initialCraving.toInt()} / 10", color = RoseDanger, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = initialCraving,
                    onValueChange = { initialCraving = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = RoseDanger,
                        activeTrackColor = RoseDanger,
                        inactiveTrackColor = BorderDark
                    )
                )

                Text(
                    text = "Observe the itch without judgment. When you tap the circle during breathing, tactile anchor neurons divert focus away from phone feeds.",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// Physical phone vibration when tapping the circle with complete safety
private fun triggerPhoneHaptic(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            val vibrator = vibratorManager?.defaultVibrator
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        }
    } catch (_: Exception) {
    }
}
