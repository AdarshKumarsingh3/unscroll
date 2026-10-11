package com.unscroll.app.ui.screens

import android.content.Context
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DailyMicroChallenge(
    val day: String,
    val emoji: String,
    val title: String,
    val description: String,
    val accentColor: Color
)

@Composable
fun StreakChallengeScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    val scope = rememberCoroutineScope()

    val challenges = remember {
        listOf(
            DailyMicroChallenge(
                day = "Monday",
                emoji = "\uD83C\uDF05", // 🌅
                title = "Morning Clarity",
                description = "No phone for first 30 min after waking",
                accentColor = TealBright
            ),
            DailyMicroChallenge(
                day = "Tuesday",
                emoji = "\uD83D\uDEB6\u200D\u2642\uFE0F", // 🚶‍♂️
                title = "Active Reset",
                description = "Replace 1 scroll session with a walk",
                accentColor = EmeraldAccent
            ),
            DailyMicroChallenge(
                day = "Wednesday",
                emoji = "\uD83D\uDEAA", // 🚪
                title = "Physical Sanctuary",
                description = "Leave phone in another room for 1 hour",
                accentColor = CyanAccent
            ),
            DailyMicroChallenge(
                day = "Thursday",
                emoji = "\u2600\uFE0F", // ☀️
                title = "Midday Fast",
                description = "No social media before noon",
                accentColor = AmberAccent
            ),
            DailyMicroChallenge(
                day = "Friday",
                emoji = "\uD83E\uDEC1", // 🫁
                title = "Nervous System Reset",
                description = "Do 3 breathing exercises today",
                accentColor = BlueVibrant
            ),
            DailyMicroChallenge(
                day = "Saturday",
                emoji = "\uD83C\uDF7D\uFE0F", // 🍽️
                title = "Present Dining",
                description = "Screen-free meal with someone",
                accentColor = OrangeAccent
            ),
            DailyMicroChallenge(
                day = "Sunday",
                emoji = "\uD83C\uDF19", // 🌙
                title = "Digital Sunset",
                description = "Full digital sunset after 8 PM",
                accentColor = PurpleAccent
            )
        )
    }

    // State stored in remember with mutableStateListOf of booleans
    val challengeCompletion = remember {
        mutableStateListOf(false, false, false, false, false, false, false)
    }

    val completedCount = challengeCompletion.count { it }
    val progressFraction = completedCount / 7f

    val quotes = remember {
        listOf(
            "The journey of 1,000 reclaimed hours starts with a single mindful choice.",
            "One win down! You took your first conscious step away from autopilot scrolling.",
            "Two days strong! Your neuroplasticity is rewiring toward deep calm.",
            "Almost halfway! Feel how much lighter your mind feels without feed pollution.",
            "Over halfway there! 4 mindful days breaking the relentless dopamine loop.",
            "Unstoppable momentum! 5 micro-habits conquered. Protect your focus.",
            "One final milestone! 6 days down, just Sunday's challenge for weekly mastery.",
            "\uD83C\uDFC6 Weekly Mastery Achieved! You have full control over your digital reality."
        )
    }

    val motivationalQuote = quotes[completedCount.coerceIn(0, 7)]

    // Streak pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "streakPulse")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fireScale"
    )

    // Animated progress indicator
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "progressAnim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientTealStart.copy(alpha = 0.85f),
                            GradientTealEnd.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        HapticFeedback.triggerClick(context)
                        SoundSynthesizer.playTick()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark.copy(alpha = 0.6f))
                        .border(1.dp, BorderDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weekly Challenge",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "7 Daily Micro-Habits to Break Doomscrolling",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Current streak badge with fire emoji and pulse
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark.copy(alpha = 0.85f))
                        .border(1.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "\uD83D\uDD25", // 🔥
                            fontSize = 16.sp,
                            modifier = Modifier.scale(fireScale)
                        )
                        Text(
                            text = "${prefs.streakDays}d",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AmberWarm
                        )
                    }
                }
            }
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Weekly Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(TealPrimary.copy(alpha = 0.4f), BorderDark)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Weekly Mastery",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${completedCount} of 7 micro-habits conquered",
                                fontSize = 13.sp,
                                color = TealLight
                            )
                        }

                        // Circular progress ring
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(74.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = 1f,
                                strokeWidth = 7.dp,
                                color = SurfaceDark,
                                modifier = Modifier.fillMaxSize()
                            )
                            CircularProgressIndicator(
                                progress = animatedProgress,
                                strokeWidth = 7.dp,
                                strokeCap = StrokeCap.Round,
                                color = if (completedCount == 7) EmeraldAccent else TealBright,
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${completedCount}/7",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Days",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = BorderDark, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Motivational quote section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceDark.copy(alpha = 0.7f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\uD83D\uDCA1", // 💡
                            fontSize = 18.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text(
                            text = motivationalQuote,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Challenge Cards Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Micro-Challenges",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Weekly reset button
                TextButton(
                    onClick = {
                        HapticFeedback.triggerClick(context)
                        SoundSynthesizer.playTick()
                        for (i in 0 until challengeCompletion.size) {
                            challengeCompletion[i] = false
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Week",
                        tint = AmberAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset Week",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AmberAccent
                    )
                }
            }

            // 7 Challenge Cards
            challenges.forEachIndexed { index, challenge ->
                val isCompleted = challengeCompletion[index]

                // Card background: gradient green on completion, CardDark when pending
                val cardBrush = if (isCompleted) {
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF0F392B),
                            Color(0xFF144E39),
                            CardDark
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            CardDark,
                            CardDark
                        )
                    )
                }

                val borderColor by animateColorAsState(
                    targetValue = if (isCompleted) EmeraldAccent.copy(alpha = 0.7f) else BorderDark,
                    animationSpec = tween(400),
                    label = "border_${index}"
                )

                AnimatedPressCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val willComplete = !isCompleted
                        challengeCompletion[index] = willComplete

                        if (willComplete) {
                            HapticFeedback.triggerSuccess(context)
                            SoundSynthesizer.playSuccessChime()

                            // Check if all 7 completed
                            val newCompletedCount = challengeCompletion.count { it }
                            if (newCompletedCount == 7) {
                                scope.launch {
                                    delay(400)
                                    SoundSynthesizer.playLevelUp()
                                }
                            }
                        } else {
                            HapticFeedback.triggerClick(context)
                            SoundSynthesizer.playTick()
                        }
                    },
                    playClickSound = false
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(cardBrush)
                            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left-side colored accent strip
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .fillMaxHeight()
                                    .background(
                                        if (isCompleted) EmeraldAccent else challenge.accentColor,
                                        shape = RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)
                                    )
                            )

                            // Main Card Content
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Emoji Icon Box
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) EmeraldAccent.copy(alpha = 0.2f)
                                            else challenge.accentColor.copy(alpha = 0.15f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isCompleted) EmeraldAccent.copy(alpha = 0.4f)
                                            else challenge.accentColor.copy(alpha = 0.3f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = challenge.emoji,
                                        fontSize = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Challenge Info
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = challenge.day.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = if (isCompleted) EmeraldAccent else challenge.accentColor
                                        )
                                        if (isCompleted) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(EmeraldAccent.copy(alpha = 0.25f))
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "COMPLETED",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldAccent
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = challenge.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = challenge.description,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Animated Checkbox Button
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) EmeraldAccent
                                            else SurfaceDark
                                        )
                                        .border(
                                            width = if (isCompleted) 0.dp else 1.5.dp,
                                            color = if (isCompleted) Color.Transparent else BorderDark,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = isCompleted,
                                        enter = scaleIn(animationSpec = spring(dampingRatio = 0.6f)) + fadeIn(),
                                        exit = scaleOut() + fadeOut()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = BgDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
