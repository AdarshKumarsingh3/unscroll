package com.unscroll.app.ui.screens

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ZenOrb(
    val id: Int,
    val color1: Color,
    val color2: Color,
    val sizeRange: IntRange,
    val noteFreq: Float
)

@Composable
fun MindfulTapScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }

    var score by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableIntStateOf(30) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var activeOrbs by remember { mutableStateOf(listOf<ZenOrb>()) }
    var showResults by remember { mutableStateOf(false) }

    val orbTemplates = remember {
        listOf(
            ZenOrb(0, TealPrimary, CyanAccent, 60..80, 523.25f),
            ZenOrb(1, PurpleAccent, PinkAccent, 50..70, 587.33f),
            ZenOrb(2, EmeraldAccent, TealBright, 55..75, 659.25f),
            ZenOrb(3, BlueCalm, IndigoAccent, 45..65, 698.46f),
            ZenOrb(4, OrangeAccent, AmberAccent, 50..70, 783.99f),
            ZenOrb(5, PinkAccent, RoseLight, 40..60, 880f)
        )
    }

    // Spawn orbs
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (timeRemaining > 0 && isPlaying) {
                delay(1200L - (score * 15L).coerceAtMost(600L)) // Gets faster
                if (activeOrbs.size < 4) {
                    val template = orbTemplates.random()
                    val newOrb = template.copy(id = (0..99999).random())
                    activeOrbs = activeOrbs + newOrb
                }
                // Remove old orbs after a while
                if (activeOrbs.size > 3) {
                    activeOrbs = activeOrbs.drop(1)
                    combo = 0 // Missed = combo reset
                }
            }
        }
    }

    // Timer
    LaunchedEffect(isPlaying, timeRemaining) {
        if (isPlaying && timeRemaining > 0) {
            delay(1000)
            timeRemaining--
            if (timeRemaining == 0) {
                isPlaying = false
                showResults = true
                prefs.addReclaimedTime(0.5f / 60f) // 30 seconds saved
                prefs.recordBreathSession()
                SoundSynthesizer.playLevelUp()
                HapticFeedback.triggerSuccess(context)
            }
        }
    }

    // Background breathing animation
    val bgTransition = rememberInfiniteTransition(label = "bg")
    val bgShift by bgTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "bgShift"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        if (isPlaying) IndigoAccent.copy(alpha = 0.05f + bgShift * 0.05f) else MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.horizontalGradient(listOf(PurpleAccent.copy(alpha = 0.15f), CyanAccent.copy(alpha = 0.15f)))
                )
                .border(1.dp, PurpleAccent.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "\uD83C\uDFAE Mindful Tap \u2022 Zen Game",
                color = PurpleAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Tap the Orbs",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Replace mindless scrolling with mindful tapping. Build presence, not addiction.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (!showResults) {
            // Stats bar
            if (isPlaying) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatChip(label = "TIME", value = "${timeRemaining}s", color = if (timeRemaining <= 10) RoseDanger else TealBright)
                    StatChip(label = "SCORE", value = "$score", color = PurpleAccent)
                    StatChip(label = "COMBO", value = "${combo}x", color = if (combo >= 5) AmberAccent else BlueCalm)
                }

                // Progress
                LinearProgressIndicator(
                    progress = (timeRemaining / 30f).coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (timeRemaining <= 10) RoseDanger else TealBright,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }

            // ORB FIELD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPlaying) PurpleAccent.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isPlaying) {
                        // Idle state
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Floating preview orbs
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                orbTemplates.take(3).forEachIndexed { i, orb ->
                                    val floatAnim = rememberInfiniteTransition(label = "float$i")
                                    val floatY by floatAnim.animateFloat(
                                        initialValue = -8f,
                                        targetValue = 8f,
                                        animationSpec = infiniteRepeatable(
                                            tween(1500 + i * 300, easing = FastOutSlowInEasing),
                                            RepeatMode.Reverse
                                        ),
                                        label = "floatY$i"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .graphicsLayer { translationY = floatY }
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(orb.color1.copy(alpha = 0.4f), orb.color2.copy(alpha = 0.1f))
                                                )
                                            )
                                            .border(2.dp, orb.color1.copy(alpha = 0.5f), CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("30-Second Challenge", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Tap orbs as they appear", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        }
                    } else {
                        // Active orbs
                        activeOrbs.forEach { orb ->
                            TappableOrb(
                                orb = orb,
                                onTap = {
                                    activeOrbs = activeOrbs.filter { it.id != orb.id }
                                    val comboBonus = if (combo >= 5) 3 else if (combo >= 3) 2 else 1
                                    score += comboBonus
                                    combo++
                                    if (combo > maxCombo) maxCombo = combo
                                    HapticFeedback.triggerClick(context)
                                    SoundSynthesizer.playSingingBowlChime(orb.noteFreq, 0.3f)
                                }
                            )
                        }

                        if (activeOrbs.isEmpty()) {
                            val waitAnim = rememberInfiniteTransition(label = "wait")
                            val waitAlpha by waitAnim.animateFloat(
                                initialValue = 0.3f, targetValue = 0.8f,
                                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                                label = "waitAlpha"
                            )
                            Text(
                                "\u2728",
                                fontSize = 40.sp,
                                modifier = Modifier.graphicsLayer { alpha = waitAlpha }
                            )
                        }
                    }
                }
            }

            // Start / Back buttons
            if (!isPlaying) {
                Button(
                    onClick = {
                        isPlaying = true
                        timeRemaining = 30
                        score = 0
                        combo = 0
                        maxCombo = 0
                        activeOrbs = emptyList()
                        showResults = false
                        HapticFeedback.triggerClick(context)
                        SoundSynthesizer.playWhoosh()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                ) {
                    Text("\uD83C\uDFAE Start 30s Challenge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                OutlinedButton(
                    onClick = {
                        HapticFeedback.triggerClick(context)
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("\u2190 Back", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // RESULTS SCREEN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(PurpleAccent.copy(alpha = 0.1f), CyanAccent.copy(alpha = 0.05f))
                            )
                        )
                        .border(1.dp, PurpleAccent.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                        .padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("\uD83C\uDFC6", fontSize = 48.sp)

                        Text(
                            "Session Complete!",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ResultStat(label = "SCORE", value = "$score", color = PurpleAccent)
                            ResultStat(label = "MAX COMBO", value = "${maxCombo}x", color = AmberAccent)
                        }

                        Text(
                            text = when {
                                score >= 20 -> "\uD83E\uDD2F Incredible focus! Your prefrontal cortex is on fire."
                                score >= 12 -> "\uD83D\uDD25 Great awareness! You're rewiring fast."
                                score >= 6 -> "\uD83D\uDCAA Solid start! Mindfulness is building."
                                else -> "\uD83C\uDF31 Every tap is progress. Keep practicing!"
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                showResults = false
                                score = 0
                                combo = 0
                                maxCombo = 0
                                timeRemaining = 30
                                activeOrbs = emptyList()
                                SoundSynthesizer.playWhoosh()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                        ) {
                            Text("\uD83D\uDD01 Play Again", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        TextButton(onClick = onNavigateBack) {
                            Text("\u2190 Back to App", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TappableOrb(orb: ZenOrb, onTap: () -> Unit) {
    val enterScale = remember { Animatable(0.3f) }
    val enterAlpha = remember { Animatable(0f) }

    LaunchedEffect(orb.id) {
        launch {
            enterScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 300f))
        }
        enterAlpha.animateTo(1f, tween(200))
    }

    val floatAnim = rememberInfiniteTransition(label = "orbFloat${orb.id}")
    val floatY by floatAnim.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            tween((1200..2000).random(), easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "floatY${orb.id}"
    )
    val floatX by floatAnim.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            tween((1400..2200).random(), easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "floatX${orb.id}"
    )

    val orbSize = remember { orb.sizeRange.random().dp }

    Box(
        modifier = Modifier
            .size(orbSize)
            .graphicsLayer {
                scaleX = enterScale.value
                scaleY = enterScale.value
                alpha = enterAlpha.value
                translationY = floatY
                translationX = floatX
            }
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        orb.color1.copy(alpha = 0.5f),
                        orb.color2.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                )
            )
            .border(
                3.dp,
                Brush.linearGradient(listOf(orb.color1, orb.color2)),
                CircleShape
            )
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Text("\u2728", fontSize = 18.sp)
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(label, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
private fun ResultStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 32.sp, fontWeight = FontWeight.Black)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}
