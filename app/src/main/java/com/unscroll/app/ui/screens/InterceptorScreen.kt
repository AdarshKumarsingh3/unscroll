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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
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
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.SoundSynthesizer
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
            "Instagram Reels" to true,
            "TikTok" to true,
            "YouTube Shorts" to true,
            "X / Twitter" to true,
            "Reddit" to true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(if (isShieldActive) EmeraldAccent.copy(alpha = 0.15f) else RoseDanger.copy(alpha = 0.15f))
                .border(
                    1.dp,
                    if (isShieldActive) EmeraldAccent.copy(alpha = 0.3f) else RoseDanger.copy(alpha = 0.3f),
                    RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isShieldActive) "● Active Android Interceptor Shield" else "○ Shield Paused",
                color = if (isShieldActive) EmeraldAccent else RoseDanger,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Reality-Check Interceptor",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            text = "Adds a 5-second mindfulness check when opening short-form video feeds.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        // Main Shield Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Interceptor Status",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Monitors app window state events",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = isShieldActive,
                    onCheckedChange = {
                        isShieldActive = it
                        prefs.interceptorActive = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealBright
                    )
                )
            }
        }

        // Accessibility Service Setup Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = TealBright)
                    Text(
                        text = "Android Accessibility Permission",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Android requires enabling the Unscroll Accessibility Service so the app can detect when feeds are opened without monitoring personal data.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enable in Android Settings", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Monitored Apps Checklist
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "TARGET FEED TRAPS",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                monitoredApps.forEachIndexed { index, (appName, enabled) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(appName, color = Color.White, fontSize = 14.sp)
                        Checkbox(
                            checked = enabled,
                            onCheckedChange = { monitoredApps[index] = appName to it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TealBright,
                                uncheckedColor = BorderDark
                            )
                        )
                    }
                    if (index < monitoredApps.size - 1) {
                        HorizontalDivider(color = BorderDark.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // Test Simulator Button
        Button(
            onClick = {
                isSimulatingPause = true
                SoundSynthesizer.playSingingBowlChime(432f, 2.5f)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = BgDark)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simulate Feed Interception", color = BgDark, fontWeight = FontWeight.Bold)
        }
    }

    // Modal Simulation of what user sees over Instagram / TikTok
    if (isSimulatingPause) {
        PauseScreenSimulationModal(
            onDismiss = { isSimulatingPause = false },
            onNavigateToUrgeSurfer = {
                isSimulatingPause = false
                onNavigateToUrgeSurfer()
            },
            onNavigateToReplacements = {
                isSimulatingPause = false
                onNavigateToReplacements()
            }
        )
    }
}

@Composable
private fun PauseScreenSimulationModal(
    onDismiss: () -> Unit,
    onNavigateToUrgeSurfer: () -> Unit,
    onNavigateToReplacements: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(5) }
    var selectedTrigger by remember { mutableStateOf("Bored") }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.2f))
                        .border(2.dp, TealBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (countdown > 0) "${countdown}s" else "✨",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Pause. Is this conscious?",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "You opened Instagram Reels. Take one deep breath.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "What is driving the craving?",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Bored", "Stressed", "Lonely").forEach { trigger ->
                        FilterChip(
                            selected = selectedTrigger == trigger,
                            onClick = { selectedTrigger = trigger },
                            label = { Text(trigger, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Button(
                    onClick = onNavigateToUrgeSurfer,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealBright)
                ) {
                    Text("🌊 Ride 90s Urge Surfer", color = BgDark, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onNavigateToReplacements,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Text("⚡ Play 30s Dopamine Bite", color = Color.White)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue for 5m budget", color = AmberAccent, fontSize = 11.sp)
            }
        }
    )
}
