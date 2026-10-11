package com.unscroll.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.shadow
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

private data class ColorModel(
    val key: String,
    val name: String,
    val color: Color,
    val partnerKey: String
)

private val GAME_PALETTE = listOf(
    ColorModel("teal", "Teal", TealBright, "emerald"),
    ColorModel("rose", "Rose", RoseDanger, "pink"),
    ColorModel("purple", "Purple", PurpleAccent, "blue"),
    ColorModel("amber", "Amber", AmberAccent, "orange"),
    ColorModel("blue", "Blue", BlueCalm, "purple"),
    ColorModel("emerald", "Emerald", EmeraldAccent, "teal"),
    ColorModel("pink", "Pink", PinkAccent, "rose"),
    ColorModel("orange", "Orange", OrangeAccent, "amber")
)

private enum class GamePhase {
    INTRO,
    PLAYING,
    RESULTS
}

@Composable
fun ColorMatchScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    var gamePhase by remember { mutableStateOf(GamePhase.INTRO) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }
    var timeRemaining by remember { mutableIntStateOf(45) }
    val reactionTimes = remember { mutableStateListOf<Long>() }
    var roundStartTime by remember { mutableLongStateOf(0L) }

    var targetColorModel by remember { mutableStateOf(GAME_PALETTE[0]) }
    var currentOptions by remember { mutableStateOf<List<ColorModel>>(emptyList()) }
    var roundIndex by remember { mutableIntStateOf(0) }

    // Visual feedback states
    var isCorrectFlash by remember { mutableStateOf(false) }
    var isWrongFlash by remember { mutableStateOf(false) }

    fun generateNewRound(newScore: Int) {
        val nextTarget = GAME_PALETTE.filter { it.key != targetColorModel.key }.random()
        val numOptions = if (newScore < 6) 4 else 6

        val optionsList = mutableListOf(nextTarget)

        // If score is higher, intentionally include partner/similar color to test focus
        if (newScore >= 6) {
            val partner = GAME_PALETTE.firstOrNull { it.key == nextTarget.partnerKey }
            if (partner != null && !optionsList.contains(partner)) {
                optionsList.add(partner)
            }
        }

        val remainingCandidates = GAME_PALETTE.filter { candidate ->
            optionsList.none { it.key == candidate.key }
        }.shuffled()

        for (candidate in remainingCandidates) {
            if (optionsList.size >= numOptions) break
            optionsList.add(candidate)
        }

        optionsList.shuffle()

        targetColorModel = nextTarget
        currentOptions = optionsList
        roundIndex++
        roundStartTime = System.currentTimeMillis()
    }

    fun startGame() {
        score = 0
        streak = 0
        bestStreak = 0
        timeRemaining = 45
        reactionTimes.clear()
        isCorrectFlash = false
        isWrongFlash = false
        generateNewRound(0)
        gamePhase = GamePhase.PLAYING
        SoundSynthesizer.playWhoosh()
    }

    // Timer countdown
    LaunchedEffect(gamePhase) {
        if (gamePhase == GamePhase.PLAYING) {
            while (timeRemaining > 0) {
                delay(1000L)
                timeRemaining--
            }

            // Time's up!
            gamePhase = GamePhase.RESULTS
            SoundSynthesizer.playLevelUp()
            HapticFeedback.triggerSuccess(context)
            prefs.addReclaimedTime(45f / 3600f) // 45 seconds reclaimed
            if (reactionTimes.isNotEmpty()) {
                val avgMs = reactionTimes.average().toLong()
                prefs.recordResponseTime(avgMs)
            }
        }
    }

    // Background animation
    val infiniteTransition = rememberInfiniteTransition(label = "bgShift")
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientShift"
    )

    val currentTargetColor = targetColorModel.color
    val flashColor by animateColorAsState(
        targetValue = when {
            isCorrectFlash -> EmeraldAccent.copy(alpha = 0.15f)
            isWrongFlash -> RoseDanger.copy(alpha = 0.2f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 180),
        label = "flashColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BgDark,
                        CardDark.copy(alpha = 0.8f + gradientShift * 0.15f),
                        BgDark
                    )
                )
            )
    ) {
        // Flash overlay for feedback
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(flashColor)
        )

        when (gamePhase) {
            GamePhase.INTRO -> {
                IntroContent(
                    onStart = { startGame() },
                    onNavigateBack = onNavigateBack
                )
            }

            GamePhase.PLAYING -> {
                PlayingContent(
                    timeRemaining = timeRemaining,
                    score = score,
                    streak = streak,
                    avgReactionTime = if (reactionTimes.isNotEmpty()) reactionTimes.average().toLong() else 0L,
                    targetColor = currentTargetColor,
                    targetName = targetColorModel.name,
                    options = currentOptions,
                    roundIndex = roundIndex,
                    onNavigateBack = onNavigateBack,
                    onOptionSelected = { chosen ->
                        val tapTime = System.currentTimeMillis()
                        val reactionMs = tapTime - roundStartTime

                        if (chosen.key == targetColorModel.key) {
                            // CORRECT TAP
                            val newScore = score + 1
                            val newStreak = streak + 1
                            score = newScore
                            streak = newStreak
                            if (newStreak > bestStreak) {
                                bestStreak = newStreak
                            }
                            reactionTimes.add(reactionMs)

                            if (newStreak >= 5 && newStreak % 5 == 0) {
                                SoundSynthesizer.playSuccessChime()
                            } else {
                                SoundSynthesizer.playTick()
                            }
                            HapticFeedback.triggerClick(context)

                            coroutineScope.launch {
                                isCorrectFlash = true
                                delay(120)
                                isCorrectFlash = false
                            }

                            generateNewRound(newScore)
                        } else {
                            // WRONG TAP
                            streak = 0
                            SoundSynthesizer.playWarning()
                            HapticFeedback.triggerClick(context)

                            coroutineScope.launch {
                                isWrongFlash = true
                                delay(160)
                                isWrongFlash = false
                            }

                            generateNewRound(score)
                        }
                    }
                )
            }

            GamePhase.RESULTS -> {
                ResultsContent(
                    score = score,
                    bestStreak = bestStreak,
                    avgReactionTime = if (reactionTimes.isNotEmpty()) reactionTimes.average().toLong() else 0L,
                    onPlayAgain = { startGame() },
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun IntroContent(
    onStart: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onNavigateBack) {
                Text(
                    text = "←",
                    color = TextPrimaryDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            // Animated Icon Badge
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(TealBright, CyanAccent, PurpleAccent, RoseDanger, AmberAccent, TealBright)
                        )
                    )
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(CardDark),
                contentAlignment = Alignment.Center
            ) {
                Text("🎯", fontSize = 42.sp)
            }

            Text(
                text = "Color Match",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark
            )

            Text(
                text = "Rapid Prefrontal Focus Training",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TealBright
            )

            Text(
                text = "Break mindless autopilot. Tap the matching hue as fast as possible to build sharp visual reflex and dopamine self-regulation.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rules Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark.copy(alpha = 0.7f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    RuleRow(icon = "⏱️", title = "45 Seconds", subtitle = "Quick, high-energy focus burst")
                    RuleRow(icon = "👁️", title = "Target Hue", subtitle = "Large circle shows the color to match")
                    RuleRow(icon = "🔥", title = "Streak Multipliers", subtitle = "Maintain momentum for success chimes")
                    RuleRow(icon = "⚡", title = "Adaptive Difficulty", subtitle = "More similar tones emerge as score rises")
                }
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealBright)
            ) {
                Text(
                    text = "Start Focus Session",
                    color = BgDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Back to App",
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RuleRow(icon: String, title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Column {
            Text(title, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = TextSecondaryDark, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PlayingContent(
    timeRemaining: Int,
    score: Int,
    streak: Int,
    avgReactionTime: Long,
    targetColor: Color,
    targetName: String,
    options: List<ColorModel>,
    roundIndex: Int,
    onNavigateBack: () -> Unit,
    onOptionSelected: (ColorModel) -> Unit
) {
    // Target circle pop animation on round change
    val targetScale = remember { Animatable(0.75f) }
    LaunchedEffect(roundIndex) {
        targetScale.snapTo(0.82f)
        targetScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Text(
                        text = "✕",
                        color = TextSecondaryDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderMetric(
                        label = "SCORE",
                        value = "${score}",
                        tint = TealBright
                    )
                    HeaderMetric(
                        label = "STREAK",
                        value = "${streak}🔥",
                        tint = if (streak >= 5) AmberAccent else TextPrimaryDark
                    )
                    HeaderMetric(
                        label = "TIME",
                        value = "${timeRemaining}s",
                        tint = if (timeRemaining <= 10) RoseDanger else TextPrimaryDark
                    )
                }
            }

            // Linear Progress Bar (progress = floatValue rule)
            LinearProgressIndicator(
                progress = (timeRemaining / 45f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (timeRemaining <= 10) RoseDanger else TealBright,
                trackColor = SurfaceDark,
                strokeCap = StrokeCap.Round
            )
        }

        // Live Reaction time badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark.copy(alpha = 0.8f))
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("⚡", fontSize = 12.sp)
            Text(
                text = if (avgReactionTime > 0) "Avg: ${avgReactionTime} ms" else "Match as fast as possible",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondaryDark
            )
        }

        // Target Circle section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "MATCH THIS COLOR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = TextSecondaryDark
            )

            // Large Target Circle with Spring Pop
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer {
                        scaleX = targetScale.value
                        scaleY = targetScale.value
                    }
                    .shadow(elevation = 20.dp, shape = CircleShape, spotColor = targetColor)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                targetColor.copy(alpha = 0.85f),
                                targetColor
                            )
                        )
                    )
                    .border(4.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Subtle center shine
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                )
            }
        }

        // Color Options Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Choose the match below",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextTertiaryDark
            )

            val columns = if (options.size > 4) 3 else 2
            val rows = options.chunked(columns)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (option in row) {
                            ColorOptionCircle(
                                option = option,
                                sizeDp = if (options.size > 4) 74.dp else 88.dp,
                                onClick = { onOptionSelected(option) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun ColorOptionCircle(
    option: ColorModel,
    sizeDp: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.86f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "optionSpring"
    )

    Box(
        modifier = Modifier
            .size(sizeDp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        option.color.copy(alpha = 0.9f),
                        option.color
                    )
                )
            )
            .border(3.dp, BorderDark.copy(alpha = 0.7f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner depth circle
        Box(
            modifier = Modifier
                .size(sizeDp * 0.4f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
        )
    }
}

@Composable
private fun HeaderMetric(
    label: String,
    value: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark.copy(alpha = 0.8f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = tint
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextTertiaryDark
        )
    }
}

@Composable
private fun ResultsContent(
    score: Int,
    bestStreak: Int,
    avgReactionTime: Long,
    onPlayAgain: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val tierTitle = when {
        score >= 35 -> "⚡ Reflex Prodigy"
        score >= 25 -> "🎯 Focus Master"
        score >= 15 -> "🔥 Steady Anchor"
        else -> "🌱 Developing Awareness"
    }

    val feedbackMsg = when {
        score >= 30 -> "Incredible neural speed! Your prefrontal cortex effortlessly overpowered mindless impulse."
        score >= 18 -> "Outstanding reaction rate. You effectively redirected dopamine seeking into intense mindful focus."
        score >= 10 -> "Solid session. With repeated training, your impulse resistance will sharpen further."
        else -> "Good effort. Every active choice disrupts unconscious habitual phone checks."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(TealBright, CyanAccent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏆", fontSize = 40.sp)
            }

            Text(
                text = "Session Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark
            )

            Text(
                text = tierTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TealBright
            )
        }

        // Stats Matrix Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResultStatItem(
                        label = "SCORE",
                        value = "$score",
                        tint = TealBright
                    )
                    ResultStatItem(
                        label = "BEST STREAK",
                        value = "${bestStreak}x",
                        tint = AmberAccent
                    )
                    ResultStatItem(
                        label = "AVG REACTION",
                        value = if (avgReactionTime > 0) "${avgReactionTime}ms" else "--",
                        tint = PurpleAccent
                    )
                }

                Divider(
                    color = BorderDark.copy(alpha = 0.6f),
                    thickness = 1.dp
                )

                Text(
                    text = feedbackMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        // Next Actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealBright)
            ) {
                Text(
                    text = "Play Again (45s)",
                    color = BgDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Text(
                    text = "Back to Dashboard",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ResultStatItem(
    label: String,
    value: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = tint
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextTertiaryDark,
            letterSpacing = 1.sp
        )
    }
}
