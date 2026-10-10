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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    // Smooth breathing
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val orbColor by animateColorAsState(
        targetValue = when (breathPhase) {
            "Inhale" -> CyanAccent
            "Hold" -> IndigoAccent
            "Exhale" -> EmeraldAccent
            else -> TealPrimary
        },
        animationSpec = tween(800), label = "orbColor"
    )

    val orbColor2 by animateColorAsState(
        targetValue = when (breathPhase) {
            "Inhale" -> BlueCalm
            "Hold" -> PurpleDeep
            "Exhale" -> TealBright
            else -> TealBright
        },
        animationSpec = tween(800), label = "orbColor2"
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
                HapticFeedback.triggerClick(context)
            }

            if (secondsRemaining == 0) {
                isRunning = false
                isFinished = true
                prefs.addReclaimedTime(0.25f)
                prefs.recordBreathSession()
                HapticFeedback.triggerSuccess(context)
                SoundSynthesizer.playLevelUp()
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
                .background(
                    Brush.horizontalGradient(listOf(IndigoAccent.copy(alpha = 0.15f), CyanAccent.copy(alpha = 0.15f)))
                )
                .border(1.dp, IndigoAccent.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "\uD83E\uDDE0 Craving Wave Protocol \u2022 90s",
                color = IndigoAccent,
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
            text = "Dopamine cravings peak for 60-90s. Ride the wave. Watch it collapse.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // MAIN BREATHING STAGE
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (!isFinished) {
                    // Outer glow ring
                    if (isRunning) {
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .scale(breathScale * 1.05f)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            orbColor.copy(alpha = 0.08f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Main breathing orb
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .scale(if (isRunning) breathScale else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        orbColor.copy(alpha = 0.25f),
                                        orbColor2.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                3.dp,
                                Brush.linearGradient(listOf(orbColor, orbColor2)),
                                CircleShape
                            )
                            .clickable {
                                if (isRunning) {
                                    tapsCount++
                                    HapticFeedback.triggerClick(context)
                                    SoundSynthesizer.playTick()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isRunning) {
                                Text(
                                    text = breathPhase.uppercase(),
                                    color = orbColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 3.sp
                                )
                                Text(
                                    text = "${secondsRemaining}s",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 52.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "\uD83D\uDC46 Tap to anchor ($tapsCount)",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text("\uD83C\uDF0A", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Ready to Surf", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("Tap Start Below", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            }
                        }
                    }

                    // Progress bar
                    LinearProgressIndicator(
                        progress = ((90f - secondsRemaining) / 90f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .padding(horizontal = 16.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = (-12).dp),
                        color = orbColor,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        strokeCap = StrokeCap.Round
                    )
                } else {
                    // VICTORY SCREEN
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(EmeraldAccent.copy(alpha = 0.3f), EmeraldAccent.copy(alpha = 0.05f))
                                    )
                                )
                                .border(3.dp, EmeraldAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("\uD83C\uDFC6", fontSize = 36.sp)
                        }

                        Text(
                            text = "Craving Collapsed!",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "You anchored $tapsCount times and rode the full 90s wave. Your dopamine receptors just recalibrated.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Button(
                            onClick = {
                                SoundSynthesizer.playWhoosh()
                                onNavigateToReplacements()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent)
                        ) {
                            Text("\u2728 Explore Dopamine Bites", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) IndigoAccent else TealPrimary
                    )
                ) {
                    Text(
                        if (isRunning) "\u23F8 Pause Wave" else "\uD83C\uDF0A Start 90s Surfer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }

                if (isRunning) {
                    IconButton(
                        onClick = {
                            isRunning = false
                            secondsRemaining = 90
                            HapticFeedback.triggerClick(context)
                            SoundSynthesizer.playWarning()
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                    ) {
                        Text("\u23F9", fontSize = 24.sp)
                    }
                }
            }

            // Stats while running
            if (isRunning) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$phaseSeconds", color = orbColor, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("PHASE", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$tapsCount", color = AmberAccent, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("TAPS", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${90 - secondsRemaining}s", color = EmeraldAccent, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("ELAPSED", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
