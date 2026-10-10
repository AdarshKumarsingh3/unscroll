package com.unscroll.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.data.ReplacementActivity
import com.unscroll.app.data.TriggerCategory
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.FocusNotificationManager
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReplacementsScreen() {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    var selectedCategory by remember { mutableStateOf<TriggerCategory?>(null) }
    
    var activeModal by remember { mutableStateOf<String?>(null) }

    val activities = remember {
        listOf(
            ReplacementActivity("1", "Reaction Time Test", TriggerCategory.BORED, "30s", "Wake up your nervous system with a reflex challenge.", "REFLEX"),
            ReplacementActivity("2", "Neuroscience Trivia", TriggerCategory.LEARN, "20s", "Learn why feeds hijack your brain.", "TRIVIA"),
            ReplacementActivity("3", "10 Rapid Squats", TriggerCategory.RESTLESS, "45s", "Flush cortisol, increase cerebral blood flow instantly.", "SQUATS"),
            ReplacementActivity("4", "Text a Friend", TriggerCategory.LONELY, "1m", "Replace parasocial scrolling with genuine connection.", "SMS"),
            ReplacementActivity("5", "Mental Model", TriggerCategory.STRESSED, "30s", "Read one powerful mental model to reframe your day.", "MENTAL"),
            ReplacementActivity("6", "Gratitude Pulse", TriggerCategory.STRESSED, "20s", "Name 3 things you're grateful for right now.", "GRATITUDE")
        )
    }

    val filteredActivities = if (selectedCategory == null) activities else activities.filter { it.category == selectedCategory }

    val categoryColors = mapOf(
        TriggerCategory.BORED to OrangeAccent,
        TriggerCategory.STRESSED to RoseDanger,
        TriggerCategory.RESTLESS to AmberAccent,
        TriggerCategory.LONELY to PinkAccent,
        TriggerCategory.PRODUCTIVE to TealBright,
        TriggerCategory.LEARN to BlueCalm
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
        // Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.horizontalGradient(listOf(PurpleAccent.copy(alpha = 0.15f), PinkAccent.copy(alpha = 0.15f)))
                )
                .border(1.dp, PurpleAccent.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "\u2728 Micro-Activity Engine",
                color = PurpleAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Dopamine Bites",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "30-second activities that satisfy the craving without trapping you in an endless loop.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Filters
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { 
                        HapticFeedback.triggerClick(context)
                        SoundSynthesizer.playTick()
                        selectedCategory = null 
                    },
                    label = { Text("All", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(TriggerCategory.values().size) { index ->
                val category = TriggerCategory.values()[index]
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { 
                        HapticFeedback.triggerClick(context)
                        SoundSynthesizer.playTick()
                        selectedCategory = category 
                    },
                    label = { Text("${category.emoji} ${category.displayName}", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = categoryColors[category] ?: TealPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Activity Cards
        filteredActivities.forEach { activity ->
            val cardColor = categoryColors[activity.category] ?: TealBright

            AnimatedPressCard(
                onClick = {
                    if (activity.actionType == "SMS") {
                        sendFriendSms(context)
                        prefs.addReclaimedTime(5f / 60f)
                        SoundSynthesizer.playSuccessChime()
                    } else {
                        SoundSynthesizer.playWhoosh()
                        activeModal = activity.actionType
                    }
                },
                playClickSound = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        cardColor.copy(alpha = 0.08f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                            .border(1.dp, cardColor.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${activity.category.emoji} ${activity.category.displayName.uppercase()}",
                                    color = cardColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(cardColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = activity.durationText,
                                        color = cardColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = activity.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black
                            )

                            Text(
                                text = activity.description,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Modals
    val onCompleteModal = {
        activeModal = null
        prefs.addReclaimedTime(5f / 60f)
        HapticFeedback.triggerSuccess(context)
        SoundSynthesizer.playLevelUp()
        FocusNotificationManager.showStreakCelebration(context, prefs.streakDays, prefs.hoursReclaimed)
    }

    when (activeModal) {
        "REFLEX" -> ReflexGameModal(prefs = prefs, onDismiss = { activeModal = null }, onComplete = onCompleteModal)
        "TRIVIA" -> TriviaModal(onDismiss = { activeModal = null }, onComplete = onCompleteModal)
        "SQUATS" -> SquatsModal(onDismiss = { activeModal = null }, onComplete = onCompleteModal)
        "MENTAL" -> MentalModelModal(onDismiss = { activeModal = null }, onComplete = onCompleteModal)
        "GRATITUDE" -> GratitudeModal(onDismiss = { activeModal = null }, onComplete = onCompleteModal)
    }
}

@Composable
private fun ReflexGameModal(prefs: UnscrollPreferences, onDismiss: () -> Unit, onComplete: () -> Unit) {
    var gameState by remember { mutableStateOf("IDLE") }
    var reactionTimeMs by remember { mutableLongStateOf(0L) }
    var startTime by remember { mutableLongStateOf(0L) }
    var bestTime by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = {
            Text(
                "\u26A1 Reflex Reaction Test",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            when (gameState) {
                                "WAITING" -> Brush.verticalGradient(listOf(RoseDanger, RoseLight))
                                "READY" -> Brush.verticalGradient(listOf(EmeraldAccent, TealBright))
                                "DONE" -> Brush.verticalGradient(listOf(GradientTealStart, GradientBlueEnd))
                                else -> Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
                            }
                        )
                        .clickable {
                            if (gameState == "WAITING") {
                                gameState = "IDLE"
                                HapticFeedback.triggerClick(context)
                                SoundSynthesizer.playWarning()
                            } else if (gameState == "READY") {
                                reactionTimeMs = System.currentTimeMillis() - startTime
                                if (bestTime == 0L || reactionTimeMs < bestTime) bestTime = reactionTimeMs
                                prefs.recordResponseTime(reactionTimeMs)
                                gameState = "DONE"
                                HapticFeedback.triggerSuccess(context)
                                SoundSynthesizer.playSuccessChime()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (gameState) {
                                "WAITING" -> "\uD83D\uDD34 WAIT..."
                                "READY" -> "\uD83D\uDFE2 TAP NOW!"
                                "DONE" -> "${reactionTimeMs}ms"
                                else -> "Tap Start"
                            },
                            color = if (gameState == "IDLE") MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            fontSize = if (gameState == "DONE") 36.sp else 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (gameState == "DONE") {
                            Text(
                                text = if (reactionTimeMs < 250) "\uD83D\uDD25 Lightning!" else if (reactionTimeMs < 400) "\u26A1 Sharp!" else "\uD83D\uDCAA Good!",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (gameState == "WAITING") {
                            Text("Too early = reset!", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                }

                if (bestTime > 0 && gameState == "DONE") {
                    Text(
                        "Best: ${bestTime}ms",
                        color = AmberAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (gameState == "IDLE" || gameState == "DONE") {
                    Button(
                        onClick = {
                            HapticFeedback.triggerClick(context)
                            gameState = "WAITING"
                            SoundSynthesizer.playHeartbeat()
                            scope.launch {
                                delay((1500L..3500L).random())
                                startTime = System.currentTimeMillis()
                                gameState = "READY"
                                HapticFeedback.triggerClick(context)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                    ) {
                        Text(
                            if (gameState == "DONE") "\uD83D\uDD01 Try Again" else "\u25B6\uFE0F Start Test",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Finish (+5m Saved)", color = TealBright, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun TriviaModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var answered by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = { Text("\uD83E\uDDE0 Neuroscience Trivia", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Which neurotransmitter is responsible for craving and seeking, rather than actual pleasure?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Button(
                    onClick = { 
                        answered = true 
                        HapticFeedback.triggerSuccess(context)
                        SoundSynthesizer.playSuccessChime()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (answered) EmeraldAccent else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        if (answered) "\u2705 Dopamine (Seeking Molecule)" else "Dopamine",
                        color = if (answered) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { 
                        answered = true 
                        HapticFeedback.triggerClick(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Serotonin", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }

                if (answered) {
                    Text(
                        text = "\uD83D\uDCA1 Dopamine surges in anticipation of reward, not upon receiving it. Feeds exploit this seeking loop relentlessly.",
                        color = TealBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Got It (+5m)", color = TealBright, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun SquatsModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var reps by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val progress = reps / 10f

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = { Text("\uD83C\uDFCB\uFE0F 10-Rep Physical Reset", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Stand up. 10 squats = restored cerebral blood flow.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                // Big counter
                Text(
                    "$reps / 10",
                    color = if (reps >= 10) EmeraldAccent else MaterialTheme.colorScheme.onSurface,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )

                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (reps >= 10) EmeraldAccent else OrangeAccent,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round
                )

                Button(
                    onClick = { 
                        if (reps < 10) {
                            reps++
                            HapticFeedback.triggerClick(context)
                            SoundSynthesizer.playTick()
                            if (reps == 10) {
                                SoundSynthesizer.playSuccessChime()
                                HapticFeedback.triggerSuccess(context)
                            }
                        } 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (reps >= 10) EmeraldAccent else OrangeAccent)
                ) {
                    Text(
                        if (reps >= 10) "\u2705 Complete!" else "\uD83D\uDCAA Tap Per Rep (+1)",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Finish (+5m)", color = TealBright, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun MentalModelModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = { Text("\uD83E\uDDE9 The Variable Reward Trap", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "A pigeon given food every time gets bored. But unpredictable rewards make it peck relentlessly until exhaustion.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PurpleAccent.copy(alpha = 0.1f))
                        .border(1.dp, PurpleAccent.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "\uD83C\uDFB0 Social feeds use the exact same slot-machine mechanic: 8 boring clips + 1 amazing clip turns your thumb into an obsessive lever.",
                        color = PurpleAccent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Understood (+5m)", color = TealBright, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun GratitudeModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var items by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = { Text("\uD83D\uDE4F Gratitude Pulse", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Close your eyes. Name 3 things you're grateful for right now. Tap the heart for each one.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                // Hearts
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(3) { i ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i < items) PinkAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    2.dp,
                                    if (i < items) PinkAccent else MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                                .clickable {
                                    if (items <= i) {
                                        items = i + 1
                                        HapticFeedback.triggerClick(context)
                                        SoundSynthesizer.playTick()
                                        if (items == 3) {
                                            SoundSynthesizer.playSuccessChime()
                                            HapticFeedback.triggerSuccess(context)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (i < items) "\u2764\uFE0F" else "\uD83E\uDD0D",
                                fontSize = 24.sp
                            )
                        }
                    }
                }

                if (items >= 3) {
                    Text(
                        "\uD83C\uDF1F Gratitude rewires your brain's default network away from seeking mode.",
                        color = EmeraldAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Done (+5m)", color = TealBright, fontWeight = FontWeight.Bold)
            }
        }
    )
}

private fun sendFriendSms(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:")
            putExtra("sms_body", "Hey! Was just thinking of you and wanted to check in. Hope you're having an awesome week!")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
