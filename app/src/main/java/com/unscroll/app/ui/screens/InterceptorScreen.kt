package com.unscroll.app.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun InterceptorScreen(
    onNavigateToUrgeSurfer: () -> Unit,
    onNavigateToReplacements: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    var isShieldActive by remember { mutableStateOf(prefs.interceptorActive) }
    var isSimulatingPause by remember { mutableStateOf(false) }

    val monitoredApps = remember {
        mutableStateListOf(
            "Instagram" to true,
            "TikTok" to true,
            "YouTube Shorts" to true,
            "X / Twitter" to true,
            "Reddit" to true,
            "Snapchat" to false
        )
    }

    // Shield pulse animation
    val pulseTransition = rememberInfiniteTransition(label = "shieldPulse")
    val shieldScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "shieldScale"
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
        // Shield status orb
        Box(
            modifier = Modifier
                .size(90.dp)
                .scale(if (isShieldActive) shieldScale else 1f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        if (isShieldActive)
                            listOf(EmeraldAccent.copy(alpha = 0.25f), EmeraldAccent.copy(alpha = 0.05f), Color.Transparent)
                        else
                            listOf(RoseDanger.copy(alpha = 0.2f), RoseDanger.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
                .border(
                    3.dp,
                    if (isShieldActive) EmeraldAccent else RoseDanger,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (isShieldActive) "\uD83D\uDEE1\uFE0F" else "\u26A0\uFE0F",
                fontSize = 36.sp
            )
        }

        Text(
            text = "Reality Check Shield",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "5-second mindfulness pause when opening feed apps. Breaks the unconscious loop.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // MAIN TOGGLE CARD
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
                            if (isShieldActive)
                                listOf(EmeraldAccent.copy(alpha = 0.1f), TealPrimary.copy(alpha = 0.05f))
                            else
                                listOf(RoseDanger.copy(alpha = 0.1f), OrangeAccent.copy(alpha = 0.05f))
                        )
                    )
                    .border(
                        1.dp,
                        if (isShieldActive) EmeraldAccent.copy(alpha = 0.3f) else RoseDanger.copy(alpha = 0.3f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Global Interceptor",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isShieldActive) "Actively monitoring feed apps" else "Shield is paused",
                            color = if (isShieldActive) EmeraldAccent else RoseDanger,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = isShieldActive,
                        onCheckedChange = {
                            HapticFeedback.triggerClick(context)
                            isShieldActive = it
                            prefs.interceptorActive = it
                            if (it) SoundSynthesizer.playSuccessChime() else SoundSynthesizer.playWarning()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldAccent,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = RoseDanger.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        // Response time stats
        val avgResp = prefs.avgResponseTimeMs
        val fastResp = prefs.fastestResponseMs
        val totalInt = prefs.totalInterceptions

        if (totalInt > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResponseStat(
                    label = "TOTAL",
                    value = "$totalInt",
                    color = TealBright,
                    modifier = Modifier.weight(1f)
                )
                ResponseStat(
                    label = "AVG RESP",
                    value = "${avgResp}ms",
                    color = BlueCalm,
                    modifier = Modifier.weight(1f)
                )
                ResponseStat(
                    label = "BEST",
                    value = "${fastResp}ms",
                    color = EmeraldAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // MONITORED APPS
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "\uD83C\uDFAF TARGET FEED TRAPS",
                    color = PurpleAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                monitoredApps.forEachIndexed { index, (appName, enabled) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (enabled)
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else
                                    Color.Transparent
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            appName,
                            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                            fontWeight = if (enabled) FontWeight.SemiBold else FontWeight.Normal
                        )
                        Switch(
                            checked = enabled,
                            onCheckedChange = {
                                HapticFeedback.triggerClick(context)
                                monitoredApps[index] = appName to it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TealPrimary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                    if (index < monitoredApps.size - 1) {
                        Divider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }

        // SIMULATE BUTTON
        AnimatedPressCard(
            onClick = {
                isSimulatingPause = true
                SoundSynthesizer.playHeartbeat()
            },
            playClickSound = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(listOf(GradientPurpleStart, GradientPurpleEnd))
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "\uD83D\uDD2C Simulate Interception",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // INTERCEPTION MODAL
    if (isSimulatingPause) {
        PauseScreenSimulationModal(
            prefs = prefs,
            onDismiss = {
                HapticFeedback.triggerClick(context)
                isSimulatingPause = false
            },
            onNavigateToUrgeSurfer = {
                HapticFeedback.triggerClick(context)
                isSimulatingPause = false
                onNavigateToUrgeSurfer()
            },
            onNavigateToReplacements = {
                HapticFeedback.triggerClick(context)
                isSimulatingPause = false
                onNavigateToReplacements()
            }
        )
    }
}

@Composable
private fun ResponseStat(label: String, value: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Text(label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
private fun PauseScreenSimulationModal(
    prefs: UnscrollPreferences,
    onDismiss: () -> Unit,
    onNavigateToUrgeSurfer: () -> Unit,
    onNavigateToReplacements: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(5) }
    var selectedTrigger by remember { mutableStateOf("") }
    val startTime = remember { System.currentTimeMillis() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        if (countdown == 0) {
            SoundSynthesizer.playSuccessChime()
            HapticFeedback.triggerSuccess(context)
            // Record response time
            val elapsed = System.currentTimeMillis() - startTime
            prefs.recordResponseTime(elapsed)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Countdown orb
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (countdown > 0) TealPrimary.copy(alpha = 0.1f) else EmeraldAccent.copy(alpha = 0.15f)
                        )
                        .border(3.dp, if (countdown > 0) TealBright else EmeraldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (countdown > 0) "${countdown}s" else "\u2713",
                        color = if (countdown > 0) MaterialTheme.colorScheme.onSurface else EmeraldAccent,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Is this conscious?",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You opened a social feed. Take one deep breath.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "WHAT'S DRIVING THE CRAVING?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("\uD83E\uDD71 Bored", "\uD83D\uDE30 Stress", "\uD83E\uDEAB Lonely").forEach { trigger ->
                        FilterChip(
                            selected = selectedTrigger == trigger,
                            onClick = {
                                selectedTrigger = trigger
                                HapticFeedback.triggerClick(context)
                                SoundSynthesizer.playTick()
                            },
                            label = { Text(trigger, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        SoundSynthesizer.playWhoosh()
                        onNavigateToUrgeSurfer()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent)
                ) {
                    Text("\uD83C\uDF0A Ride 90s Urge Surfer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                OutlinedButton(
                    onClick = {
                        SoundSynthesizer.playWhoosh()
                        onNavigateToReplacements()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, PurpleAccent.copy(alpha = 0.5f))
                ) {
                    Text("\u2728 Play 30s Dopamine Bite", color = PurpleAccent, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, enabled = countdown == 0) {
                Text(
                    if (countdown > 0) "Wait ${countdown}s..." else "Continue (5m budget)",
                    color = if (countdown > 0) MaterialTheme.colorScheme.onSurfaceVariant else AmberAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
