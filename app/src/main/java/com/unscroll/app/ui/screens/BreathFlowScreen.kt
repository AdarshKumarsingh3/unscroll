package com.unscroll.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class BreathPhase(val title: String, val instruction: String, val durationSeconds: Int) {
    INHALE("INHALE", "Inhale deeply through your nose", 4),
    HOLD("HOLD", "Hold your breath gently", 7),
    EXHALE("EXHALE", "Exhale completely through your mouth", 8)
}

@Composable
fun BreathFlowScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }

    var isRunning by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var phaseSecondsRemaining by remember { mutableIntStateOf(4) }
    var completedCycles by remember { mutableIntStateOf(0) }
    var totalElapsedSeconds by remember { mutableIntStateOf(0) }

    val circleScale = remember { Animatable(0.75f) }
    val glowScale = remember { Animatable(0.85f) }

    // Breathing loop and animations
    LaunchedEffect(isRunning) {
        if (!isRunning) {
            circleScale.animateTo(0.75f, animationSpec = tween(500))
            glowScale.animateTo(0.85f, animationSpec = tween(500))
            return@LaunchedEffect
        }

        while (isRunning) {
            // 1. INHALE (4 seconds: expands)
            currentPhase = BreathPhase.INHALE
            SoundSynthesizer.playTick()
            phaseSecondsRemaining = 4
            coroutineScope {
                launch {
                    circleScale.animateTo(
                        targetValue = 1.25f,
                        animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    glowScale.animateTo(
                        targetValue = 1.4f,
                        animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    for (s in 4 downTo 1) {
                        phaseSecondsRemaining = s
                        delay(1000L)
                        totalElapsedSeconds++
                    }
                }
            }
            if (!isRunning) break

            // 2. HOLD (7 seconds: holds)
            currentPhase = BreathPhase.HOLD
            SoundSynthesizer.playTick()
            phaseSecondsRemaining = 7
            coroutineScope {
                launch {
                    circleScale.animateTo(
                        targetValue = 1.28f,
                        animationSpec = tween(durationMillis = 7000, easing = LinearEasing)
                    )
                }
                launch {
                    glowScale.animateTo(
                        targetValue = 1.35f,
                        animationSpec = tween(durationMillis = 7000, easing = LinearEasing)
                    )
                }
                launch {
                    for (s in 7 downTo 1) {
                        phaseSecondsRemaining = s
                        delay(1000L)
                        totalElapsedSeconds++
                    }
                }
            }
            if (!isRunning) break

            // 3. EXHALE (8 seconds: contracts)
            currentPhase = BreathPhase.EXHALE
            SoundSynthesizer.playTick()
            phaseSecondsRemaining = 8
            coroutineScope {
                launch {
                    circleScale.animateTo(
                        targetValue = 0.75f,
                        animationSpec = tween(durationMillis = 8000, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    glowScale.animateTo(
                        targetValue = 0.85f,
                        animationSpec = tween(durationMillis = 8000, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    for (s in 8 downTo 1) {
                        phaseSecondsRemaining = s
                        delay(1000L)
                        totalElapsedSeconds++
                    }
                }
            }
            if (!isRunning) break

            // Cycle finished
            completedCycles++
            if (completedCycles == 5) {
                HapticFeedback.triggerSuccess(context)
                prefs.recordBreathSession()
                SoundSynthesizer.playSuccessChime()
            } else if (completedCycles > 5 && completedCycles % 5 == 0) {
                HapticFeedback.triggerSuccess(context)
                prefs.recordBreathSession()
            }
        }
    }

    // Dynamic phase colors
    val phaseAccentColor by animateColorAsState(
        targetValue = when (currentPhase) {
            BreathPhase.INHALE -> CyanAccent
            BreathPhase.HOLD -> IndigoAccent
            BreathPhase.EXHALE -> EmeraldAccent
        },
        animationSpec = tween(600),
        label = "phaseColor"
    )

    // Motivational text based on completed cycles
    val motivationalText = when {
        !isRunning && completedCycles == 0 -> "Take a mindful break. 4-7-8 breathing soothes your nervous system and stops compulsive doomscrolling."
        completedCycles == 0 -> "Draw attention into this moment. Notice every breath in and out."
        completedCycles == 1 -> "1 cycle completed. Your heart rate is steadily calming."
        completedCycles == 2 -> "2 cycles done. Notice how the craving to check your feed dissipates."
        completedCycles == 3 -> "3 cycles. Mindful awareness returning to full clarity."
        completedCycles == 4 -> "4 cycles! One more to complete a powerful reset session."
        completedCycles == 5 -> "🎉 5 cycles reached! Urge defeated and mindful session logged."
        completedCycles in 6..9 -> "${completedCycles} cycles! Deep mental peace and presence anchored."
        else -> "${completedCycles} cycles! You possess complete mastery over your impulses."
    }

    val minutes = totalElapsedSeconds / 60
    val seconds = totalElapsedSeconds % 60
    val formattedTime = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Breath Flow",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "4-7-8 Parasympathetic Reset",
                        style = MaterialTheme.typography.bodySmall,
                        color = TealBright
                    )
                }

                IconButton(
                    onClick = {
                        isRunning = false
                        completedCycles = 0
                        totalElapsedSeconds = 0
                        phaseSecondsRemaining = 4
                        currentPhase = BreathPhase.INHALE
                        HapticFeedback.triggerClick(context)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Motivational Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(phaseAccentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = phaseAccentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = motivationalText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryDark,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Phase indicator pill
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = phaseAccentColor.copy(alpha = 0.15f),
                border = BorderStroke(1.5.dp, phaseAccentColor.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(phaseAccentColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = currentPhase.title,
                        color = phaseAccentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "• ${currentPhase.durationSeconds}s",
                        color = phaseAccentColor.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentPhase.instruction,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Breathing Circle with Outer Glow Ring
            Box(
                modifier = Modifier
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Glow Ring
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .scale(glowScale.value)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    phaseAccentColor.copy(alpha = 0.28f),
                                    phaseAccentColor.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    CyanAccent.copy(alpha = 0.7f),
                                    TealPrimary.copy(alpha = 0.2f),
                                    CyanAccent.copy(alpha = 0.7f)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Secondary soft pulse ring
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scale(circleScale.value * 1.08f)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    TealLight.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Central breathing circle with TealPrimary to CyanAccent gradient
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(circleScale.value)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    CyanAccent,
                                    TealBright,
                                    TealPrimary
                                )
                            )
                        )
                        .border(
                            width = 3.dp,
                            color = phaseAccentColor.copy(alpha = 0.85f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentPhase.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${phaseSecondsRemaining}s",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Session Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cycles Counter
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cycles Completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedCycles",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TealBright
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Target: 5",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiaryDark
                        )
                    }
                }

                // Session Duration Timer
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Session Timer",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Total elapsed",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiaryDark
                        )
                    }
                }

                // Lifetime Breath Sessions
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "All-Time Saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${prefs.totalBreathSessions}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = EmeraldAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Sessions",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress towards 5-cycle milestone
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "5-Cycle Mind Reset Goal",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimaryDark
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (completedCycles >= 5) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = "${completedCycles}/5",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (completedCycles >= 5) EmeraldAccent else TealBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progressFloat = (completedCycles / 5f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = progressFloat,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (completedCycles >= 5) EmeraldAccent else TealBright,
                        trackColor = BorderDark,
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start / Pause Gradient Button
            AnimatedPressCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = if (isRunning) {
                            Brush.horizontalGradient(listOf(RoseDanger, OrangeAccent))
                        } else {
                            Brush.horizontalGradient(listOf(TealPrimary, CyanAccent))
                        }
                    ),
                onClick = {
                    if (isRunning) {
                        isRunning = false
                    } else {
                        SoundSynthesizer.playSingingBowlChime()
                        isRunning = true
                    }
                },
                playClickSound = true
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isRunning) "Pause Session" else if (completedCycles > 0) "Resume Breath Flow" else "Begin Breath Flow",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
