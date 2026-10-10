package com.unscroll.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.R
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.FocusNotificationManager
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import java.util.Locale

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    
    var streak by remember { mutableIntStateOf(prefs.streakDays) }
    var hoursReclaimed by remember { mutableFloatStateOf(prefs.hoursReclaimed) }
    var urgesDefeated by remember { mutableIntStateOf(prefs.urgesDefeatedCount) }
    var breathSessions by remember { mutableIntStateOf(prefs.totalBreathSessions) }
    var interceptions by remember { mutableIntStateOf(prefs.totalInterceptions) }
    var avgResponse by remember { mutableLongStateOf(prefs.avgResponseTimeMs) }
    var fastestResponse by remember { mutableLongStateOf(prefs.fastestResponseMs) }

    // Animated entrance
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimation = true }

    val animatedHours by animateFloatAsState(
        targetValue = if (startAnimation) hoursReclaimed else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "hours"
    )
    val animatedStreak by animateIntAsState(
        targetValue = if (startAnimation) streak else 0,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "streak"
    )
    val animatedUrges by animateIntAsState(
        targetValue = if (startAnimation) urgesDefeated else 0,
        animationSpec = tween(1300, easing = FastOutSlowInEasing),
        label = "urges"
    )

    // Pulsing glow for streak badge
    val pulseTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero section with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            TealPrimary.copy(alpha = 0.15f),
                            PurpleAccent.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Animated streak orb
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(if (streak > 0) glowScale else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (streak >= 7) EmeraldAccent.copy(alpha = 0.3f) else TealPrimary.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            3.dp,
                            if (streak >= 7) EmeraldAccent else TealBright,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$animatedStreak",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "DAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (streak >= 7) EmeraldAccent else TealBright,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Reclaimed Life",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (hoursReclaimed > 0) "Every second counts. Keep building." else "Start surfing urges to build your stats.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // PRIMARY KPI ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                title = "HOURS SAVED",
                value = String.format(Locale.US, "%.1f", animatedHours),
                icon = "\u23F0",
                subtitle = if (hoursReclaimed > 0) "Redirected from feeds" else "Start surfing!",
                gradientColors = listOf(GradientTealStart, GradientTealEnd),
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                title = "URGES BEATEN",
                value = "$animatedUrges",
                icon = "\uD83D\uDCAA",
                subtitle = "Cravings conquered",
                gradientColors = listOf(GradientPurpleStart, GradientPurpleEnd),
                modifier = Modifier.weight(1f)
            )
        }

        // ANALYTICS ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MiniStat(
                label = "BREATH",
                value = "$breathSessions",
                color = BlueCalm,
                modifier = Modifier.weight(1f)
            )
            MiniStat(
                label = "INTERCEPTS",
                value = "$interceptions",
                color = AmberAccent,
                modifier = Modifier.weight(1f)
            )
            MiniStat(
                label = "AVG RESP",
                value = if (avgResponse > 0) "${avgResponse}ms" else "--",
                color = EmeraldAccent,
                modifier = Modifier.weight(1f)
            )
            MiniStat(
                label = "BEST",
                value = if (fastestResponse > 0) "${fastestResponse}ms" else "--",
                color = PinkAccent,
                modifier = Modifier.weight(1f)
            )
        }

        // ADD TIME BUTTON
        AnimatedPressCard(
            onClick = {
                prefs.addReclaimedTime(0.25f)
                hoursReclaimed = prefs.hoursReclaimed
                urgesDefeated = prefs.urgesDefeatedCount
                streak = prefs.streakDays
                HapticFeedback.triggerSuccess(context)
                SoundSynthesizer.playSuccessChime()
                FocusNotificationManager.showStreakCelebration(context, streak, hoursReclaimed)
            },
            playClickSound = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(listOf(TealPrimary, CyanAccent))
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\u2728", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Surfed an Urge (+15 min)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // REWIRING ROADMAP
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "\uD83E\uDDE0 RECEPTOR REWIRING ROADMAP",
                    color = PurpleAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                MilestoneItem("\uD83D\uDCA1 Day 1: Loop Awareness", "First unconscious open paused", streak >= 1)
                MilestoneItem("\uD83D\uDD25 Day 3: Craving Peak Master", "Riding the 90s wave", streak >= 3)
                MilestoneItem("\u26A1 Day 7: Baseline Reset", "Receptors resensitizing", streak >= 7)
                MilestoneItem("\uD83C\uDFAF Day 14: Deep Flow", "Prefrontal cortex restored", streak >= 14)
                MilestoneItem("\uD83D\uDE80 Day 30: Algorithm-Free", "Zero automatic compulsions", streak >= 30)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: String,
    subtitle: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            gradientColors[0].copy(alpha = 0.15f),
                            gradientColors[1].copy(alpha = 0.05f)
                        )
                    )
                )
                .border(1.dp, gradientColors[0].copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(title, color = gradientColors[0], fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
                Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
    }
}

@Composable
private fun MiniStat(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(label, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
private fun MilestoneItem(title: String, desc: String, achieved: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (achieved)
                    Brush.horizontalGradient(listOf(EmeraldAccent.copy(alpha = 0.1f), EmeraldAccent.copy(alpha = 0.02f)))
                else
                    Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
            )
            .border(
                1.dp,
                if (achieved) EmeraldAccent.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (achieved) EmeraldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (achieved) {
                Text("\u2713", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                color = if (achieved) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}
