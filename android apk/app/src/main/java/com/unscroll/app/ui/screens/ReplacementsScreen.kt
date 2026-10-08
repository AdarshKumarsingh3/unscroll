package com.unscroll.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.unscroll.app.data.TriggerCategory
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ReplacementsScreen(
    onNavigateToFocus: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    var selectedCategory by remember { mutableStateOf(TriggerCategory.BORED) }

    // Active interactive mini-activity modal
    var activeModal by remember { mutableStateOf<String?>(null) }

    // Audio soundscape state
    var isRainPlaying by remember { mutableStateOf(SoundSynthesizer.isAmbientActive()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
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
                text = "⚡ Section 5: Replacement Activity Library • 30 to 90s",
                color = TealLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Micro-Replacement Bites",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            text = "Doomscrolling is an unconscious attempt to fix boredom, anxiety, or fatigue. Choose what your body is actually asking for:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        // Emotion Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(TriggerCategory.values()) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) TealPrimary else SurfaceDark)
                        .border(1.dp, if (isSelected) TealBright else BorderDark, RoundedCornerShape(16.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(cat.emoji, fontSize = 16.sp)
                        Text(
                            text = cat.displayName,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Activities for Selected Category
        when (selectedCategory) {
            TriggerCategory.BORED -> {
                BiteCard(
                    title = "30s Reflex Sprint",
                    duration = "30s",
                    desc = "Test your neuro-reaction speed against dopamine sluggishness.",
                    onLaunch = { activeModal = "reflex" }
                )
                BiteCard(
                    title = "Daily Dopamine Trivia Bite",
                    duration = "45s",
                    desc = "A quick curiosity puzzle: How algorithms hijack the basal ganglia.",
                    onLaunch = { activeModal = "trivia" }
                )
            }
            TriggerCategory.STRESSED -> {
                BiteCard(
                    title = "Synthesized Rain Soundscape",
                    duration = "90s",
                    desc = if (isRainPlaying) "● Rain sound is playing in background" else "Acoustic blanket to soothe overstimulated nerves.",
                    actionLabel = if (isRainPlaying) "Stop Rain" else "Play Ambient Rain",
                    onLaunch = {
                        if (isRainPlaying) {
                            SoundSynthesizer.stopAmbientRain()
                            isRainPlaying = false
                        } else {
                            SoundSynthesizer.startAmbientRain()
                            isRainPlaying = true
                        }
                    }
                )
            }
            TriggerCategory.RESTLESS -> {
                BiteCard(
                    title = "10-Rep Physical Squat Reset",
                    duration = "45s",
                    desc = "Flush cortisol and pump oxygen into your prefrontal cortex.",
                    onLaunch = { activeModal = "squats" }
                )
            }
            TriggerCategory.LONELY -> {
                BiteCard(
                    title = "Text One Friend Spark",
                    duration = "30s",
                    desc = "Replace the parasocial feed illusion with real connection.",
                    actionLabel = "Launch SMS / WhatsApp",
                    onLaunch = {
                        sendFriendSms(context)
                        prefs.addReclaimedTime(0.1f)
                    }
                )
            }
            TriggerCategory.PRODUCTIVE -> {
                BiteCard(
                    title = "Single Priority Lock-In",
                    duration = "60s",
                    desc = "Write the 1 domino task that matters today and start 25m flow.",
                    actionLabel = "Take to Focus Room",
                    onLaunch = onNavigateToFocus
                )
            }
            TriggerCategory.LEARN -> {
                BiteCard(
                    title = "60-Second Mental Model",
                    duration = "60s",
                    desc = "The Variable Reward Trap: Why unpredictable rewards hook pigeons and human thumbs.",
                    onLaunch = { activeModal = "model" }
                )
            }
        }
    }

    // Interactive Modals
    when (activeModal) {
        "reflex" -> ReflexGameModal(
            onDismiss = { activeModal = null },
            onComplete = {
                prefs.addReclaimedTime(0.1f)
                activeModal = null
            }
        )
        "trivia" -> TriviaModal(
            onDismiss = { activeModal = null },
            onComplete = {
                prefs.addReclaimedTime(0.1f)
                activeModal = null
            }
        )
        "squats" -> SquatsModal(
            onDismiss = { activeModal = null },
            onComplete = {
                prefs.addReclaimedTime(0.15f)
                activeModal = null
            }
        )
        "model" -> MentalModelModal(
            onDismiss = { activeModal = null },
            onComplete = {
                prefs.addReclaimedTime(0.1f)
                activeModal = null
            }
        )
    }
}

@Composable
private fun BiteCard(
    title: String,
    duration: String,
    desc: String,
    actionLabel: String = "Launch Activity Now",
    onLaunch: () -> Unit
) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = duration,
                    color = TealBright,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("MICRO-BITE", color = TextTertiary, fontSize = 10.sp)
            }

            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)

            Button(
                onClick = onLaunch,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardDark)
            ) {
                Text(actionLabel, color = TealLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// 1. Reflex Sprint Mini-Game Modal
@Composable
private fun ReflexGameModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var gameState by remember { mutableStateOf("IDLE") } // IDLE, WAITING, READY, DONE
    var reactionTimeMs by remember { mutableLongStateOf(0L) }
    var startTime by remember { mutableLongStateOf(0L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Reflex Reaction Test", color = Color.White) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when (gameState) {
                                "WAITING" -> RoseDanger
                                "READY" -> EmeraldAccent
                                "DONE" -> TealPrimary
                                else -> CardDark
                            }
                        )
                        .clickable {
                            if (gameState == "WAITING") {
                                gameState = "IDLE"
                            } else if (gameState == "READY") {
                                reactionTimeMs = System.currentTimeMillis() - startTime
                                gameState = "DONE"
                                SoundSynthesizer.playSingingBowlChime(660f, 1f)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (gameState) {
                            "WAITING" -> "WAIT FOR GREEN..."
                            "READY" -> "TAP NOW!"
                            "DONE" -> "${reactionTimeMs}ms (Super Sharp!)"
                            else -> "Tap Start below"
                        },
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                if (gameState == "IDLE" || gameState == "DONE") {
                    Button(
                        onClick = {
                            gameState = "WAITING"
                            Thread {
                                Thread.sleep((1500..3500).random().toLong())
                                startTime = System.currentTimeMillis()
                                gameState = "READY"
                            }.start()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealBright)
                    ) {
                        Text(if (gameState == "DONE") "Try Again" else "Start Test", color = BgDark)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Finish (+5m Saved)", color = TealLight)
            }
        }
    )
}

// 2. Trivia Modal
@Composable
private fun TriviaModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var answered by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Neuroscience Trivia Bite", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Which neurotransmitter is responsible for craving and seeking, rather than the actual pleasure of satisfaction?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Button(
                    onClick = { answered = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (answered) EmeraldAccent else CardDark)
                ) {
                    Text(if (answered) "✓ Dopamine (Seeking Molecule)" else "Dopamine", color = Color.White)
                }

                Button(
                    onClick = { answered = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CardDark)
                ) {
                    Text("Serotonin (Contentment)", color = TextSecondary)
                }

                if (answered) {
                    Text(
                        text = "💡 Correct! Dopamine surges in anticipation of reward, not upon receiving it. Feeds exploit this seeking loop.",
                        color = TealLight,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Got It (+10m Saved)", color = TealLight)
            }
        }
    )
}

// 3. Physical Squats Modal
@Composable
private fun SquatsModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var reps by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = { Text("10-Rep Physical Reset", color = Color.White) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stand up from your desk. Do 10 squats to restore cerebral blood flow.", color = TextSecondary, fontSize = 12.sp)

                Text("$reps / 10", color = TealLight, fontSize = 36.sp, fontWeight = FontWeight.Black)

                Button(
                    onClick = { if (reps < 10) reps++ },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Tap Per Rep (+1)", color = Color.White)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Finish (+10m Saved)", color = TealLight)
            }
        }
    )
}

// 4. Mental Model Modal
@Composable
private fun MentalModelModal(onDismiss: () -> Unit, onComplete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = { Text("The Variable Reward Trap", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "A pigeon given a food pellet every time gets bored quickly. But a pigeon given food unpredictably pecks relentlessly until exhaustion.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = "Social feeds use the exact same slot-machine mechanic: 8 boring clips followed by 1 amazing clip turns your thumb into an obsessive lever.",
                    color = TealLight,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onComplete) {
                Text("Understood (+10m Saved)", color = TealLight)
            }
        }
    )
}

private fun sendFriendSms(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:")
        putExtra("sms_body", "Hey! Was just thinking of you and wanted to check in. Hope you're having an awesome week!")
    }
    context.startActivity(intent)
}
