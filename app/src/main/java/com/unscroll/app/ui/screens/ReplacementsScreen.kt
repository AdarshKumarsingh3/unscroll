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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    
    var activeModal by remember { mutableStateOf<String?>(null) } // "REFLEX", "TRIVIA", "SQUATS", "MENTAL"

    val activities = remember {
        listOf(
            ReplacementActivity("1", "Reaction Time Test", TriggerCategory.BORED, "30s", "Wake up your nervous system with a reflex challenge.", "REFLEX"),
            ReplacementActivity("2", "Neuroscience Trivia", TriggerCategory.LEARN, "20s", "Learn why feeds hijack your brain.", "TRIVIA"),
            ReplacementActivity("3", "10 Rapid Squats", TriggerCategory.RESTLESS, "45s", "Flush cortisol, increase cerebral blood flow instantly.", "SQUATS"),
            ReplacementActivity("4", "Text a Friend", TriggerCategory.LONELY, "1m", "Replace parasocial scrolling with genuine connection.", "SMS"),
            ReplacementActivity("5", "Mental Model", TriggerCategory.STRESSED, "30s", "Read one powerful mental model to reframe your day.", "MENTAL")
        )
    }

    val filteredActivities = if (selectedCategory == null) activities else activities.filter { it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero
        Text(
            text = "Dopamine Bites",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "30-second micro-activities that satisfy the craving for stimulation without trapping you in an endless loop.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                        selectedCategory = category 
                    },
                    label = { Text(" ", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Cards
        filteredActivities.forEach { activity ->
            AnimatedPressCard(
                onClick = {
                    if (activity.actionType == "SMS") {
                        sendFriendSms(context)
                        prefs.addReclaimedTime(5f / 60f)
                    } else {
                        activeModal = activity.actionType
                    }
                },
                playClickSound = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = " ",
                                color = TealBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = activity.durationText,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = activity.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = activity.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Modals
    val onCompleteModal = {
        activeModal = null
        prefs.addReclaimedTime(5f / 60f) // 5 mins saved
        HapticFeedback.triggerSuccess(context)
        FocusNotificationManager.showStreakCelebration(context, prefs.streakDays, prefs.hoursReclaimed)
    }

    when (activeModal) {
        "REFLEX" -> ReflexGameModal(onDismiss = { activeModal = null }, onComplete = onCompleteModal)
        "TRIVIA" -> TriviaModal(onDismiss = { activeModal = null }, onComplete = onCompleteModal)
        "SQUATS" -> SquatsModal(onDismiss = { activeModal = null }, onComplete = onCompleteModal)
        "MENTAL" -> MentalModelModal(onDismiss = { activeModal = null }, onComplete = onCompleteModal)
    }
}

@Composable
private fun ReflexGameModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var gameState by remember { mutableStateOf("IDLE") }
    var reactionTimeMs by remember { mutableLongStateOf(0L) }
    var startTime by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = { Text("Reflex Reaction Test", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
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
                                "WAITING" -> RoseDanger
                                "READY" -> EmeraldAccent
                                "DONE" -> TealPrimary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .clickable {
                            if (gameState == "WAITING") {
                                gameState = "IDLE"
                                HapticFeedback.triggerClick(context)
                            } else if (gameState == "READY") {
                                reactionTimeMs = System.currentTimeMillis() - startTime
                                gameState = "DONE"
                                HapticFeedback.triggerSuccess(context)
                                SoundSynthesizer.playSingingBowlChime(660f, 1f)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (gameState) {
                            "WAITING" -> "WAIT FOR GREEN..."
                            "READY" -> "TAP NOW!"
                            "DONE" -> "ms (Super Sharp!)"
                            else -> "Tap Start below"
                        },
                        color = if (gameState == "IDLE") MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                if (gameState == "IDLE" || gameState == "DONE") {
                    Button(
                        onClick = {
                            HapticFeedback.triggerClick(context)
                            gameState = "WAITING"
                            scope.launch {
                                delay((1500L..3500L).random())
                                startTime = System.currentTimeMillis()
                                gameState = "READY"
                                HapticFeedback.triggerClick(context) // Tiny buzz when it turns green
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text(if (gameState == "DONE") "Try Again" else "Start Test", color = Color.White, fontWeight = FontWeight.Bold)
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
        title = { Text("Neuroscience Trivia Bite", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Which neurotransmitter is responsible for craving and seeking, rather than the actual pleasure of satisfaction?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )

                Button(
                    onClick = { 
                        answered = true 
                        HapticFeedback.triggerClick(context)
                        SoundSynthesizer.playSingingBowlChime(432f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (answered) EmeraldAccent else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(if (answered) " Dopamine (Seeking Molecule)" else "Dopamine", color = if (answered) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { 
                        answered = true 
                        HapticFeedback.triggerClick(context)
                        SoundSynthesizer.playSingingBowlChime(432f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Serotonin (Contentment)", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }

                if (answered) {
                    Text(
                        text = " Correct! Dopamine surges in anticipation of reward, not upon receiving it. Feeds exploit this seeking loop.",
                        color = TealBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Got It (+5m Saved)", color = TealBright, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun SquatsModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var reps by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = { Text("10-Rep Physical Reset", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stand up. Do 10 squats to restore cerebral blood flow instantly.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)

                Text(" / 10", color = TealBright, fontSize = 48.sp, fontWeight = FontWeight.Black)

                Button(
                    onClick = { 
                        if (reps < 10) {
                            reps++
                            HapticFeedback.triggerClick(context)
                        } 
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Tap Per Rep (+1)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
private fun MentalModelModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = { Text("The Variable Reward Trap", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "A pigeon given a food pellet every time gets bored quickly. But a pigeon given food unpredictably pecks relentlessly until exhaustion.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                Text(
                    text = "Social feeds use the exact same slot-machine mechanic: 8 boring clips followed by 1 amazing clip turns your thumb into an obsessive lever.",
                    color = TealBright,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Understood (+5m Saved)", color = TealBright, fontWeight = FontWeight.Bold)
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
