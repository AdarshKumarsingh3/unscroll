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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
            "YouTube" to true,
            "X / Twitter" to true,
            "Reddit" to true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (isShieldActive) "🛡️ Active Interceptor Shield" else "⏸️ Shield Paused",
                color = if (isShieldActive) EmeraldAccent else RoseDanger,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Reality Check",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Adds a 5-second mindfulness check when opening feeds to break the unconscious loop.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Main Shield Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
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
                        text = "Monitors app window state events",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
                Switch(
                    checked = isShieldActive,
                    onCheckedChange = {
                        HapticFeedback.triggerClick(context)
                        isShieldActive = it
                        prefs.interceptorActive = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmeraldAccent
                    )
                )
            }
        }

        // Monitored Apps Checklist
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "TARGET FEED TRAPS",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                monitoredApps.forEachIndexed { index, (appName, enabled) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(appName, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Checkbox(
                            checked = enabled,
                            onCheckedChange = { 
                                HapticFeedback.triggerClick(context)
                                monitoredApps[index] = appName to it 
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TealPrimary,
                                uncheckedColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                    if (index < monitoredApps.size - 1) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // Test Simulator Button
        AnimatedPressCard(
            onClick = {
                isSimulatingPause = true
                SoundSynthesizer.playSingingBowlChime(432f, 2.5f)
            },
            playClickSound = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(EmeraldAccent)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(R.drawable.ic_nav_urge), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Simulate Interception", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Modal Simulation
    if (isSimulatingPause) {
        PauseScreenSimulationModal(
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
        if (countdown == 0) {
            SoundSynthesizer.playSingingBowlChime(600f, 1f)
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
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.1f))
                        .border(3.dp, if (countdown > 0) TealBright else EmeraldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (countdown > 0) "s" else "✓",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Is this conscious?",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You opened a social feed. Take one deep breath.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                    text = "WHAT IS DRIVING THE CRAVING?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Bored 🥱", "Stressed 😰", "Lonely 🪫").forEach { trigger ->
                        FilterChip(
                            selected = selectedTrigger == trigger,
                            onClick = { selectedTrigger = trigger },
                            label = { Text(trigger, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                
                Spacer(modifier=Modifier.height(8.dp))

                Button(
                    onClick = onNavigateToUrgeSurfer,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("🌊 Ride 90s Urge Surfer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = onNavigateToReplacements,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("✨ Play 30s Dopamine Bite", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, enabled = countdown == 0) {
                Text(if (countdown > 0) "Wait..." else "Continue for 5m budget", color = if (countdown > 0) MaterialTheme.colorScheme.onSurfaceVariant else AmberAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}
