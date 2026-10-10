package com.unscroll.app.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.unscroll.app.data.FocusPeer
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.FocusNotificationManager
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun FocusRoomsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    var isFocusing by remember { mutableStateOf(false) }
    var minutesRemaining by remember { mutableIntStateOf(25) }
    var secondsRemaining by remember { mutableIntStateOf(0) }
    var isRainPlaying by remember { mutableStateOf(false) }

    val peers = remember {
        listOf(
            FocusPeer("1", "Elena", "\uD83C\uDDEA\uD83C\uDDF8", "Studying for Med Boards", 14),
            FocusPeer("2", "Kenji", "\uD83C\uDDEF\uD83C\uDDF5", "Coding Frontend", 42),
            FocusPeer("3", "Sarah", "\uD83C\uDDFA\uD83C\uDDF8", "Writing Chapter 3", 8),
            FocusPeer("4", "David", "\uD83C\uDDEC\uD83C\uDDE7", "Deep Work Sprint", 112),
            FocusPeer("5", "Priya", "\uD83C\uDDEE\uD83C\uDDF3", "Research Paper", 67),
            FocusPeer("6", "Lucas", "\uD83C\uDDE7\uD83C\uDDF7", "Piano Practice", 23)
        )
    }

    // Timer ring pulse
    val pulseTransition = rememberInfiniteTransition(label = "timerPulse")
    val ringAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
    )
    val ringScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringScale"
    )

    LaunchedEffect(isFocusing) {
        if (isFocusing) {
            FocusNotificationManager.showOngoingFocusNotification(context, "${minutesRemaining}m", "Deep Work")
            while (minutesRemaining > 0 || secondsRemaining > 0) {
                delay(1000)
                if (secondsRemaining == 0) {
                    minutesRemaining--
                    secondsRemaining = 59
                    FocusNotificationManager.showOngoingFocusNotification(context, "${minutesRemaining}m", "Deep Work")
                } else {
                    secondsRemaining--
                }

                if (minutesRemaining == 0 && secondsRemaining == 0) {
                    isFocusing = false
                    HapticFeedback.triggerSuccess(context)
                    SoundSynthesizer.playLevelUp()
                    prefs.addReclaimedTime(25f / 60f)
                    val best = prefs.longestFocusMinutes
                    if (25 > best) prefs.longestFocusMinutes = 25
                    FocusNotificationManager.cancelFocusNotification(context)
                    if (isRainPlaying) {
                        SoundSynthesizer.stopAmbientRain()
                        isRainPlaying = false
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                HapticFeedback.triggerClick(context)
                if (isRainPlaying) {
                    SoundSynthesizer.stopAmbientRain()
                    isRainPlaying = false
                }
                onNavigateBack()
            }) {
                Icon(painter = painterResource(R.drawable.ic_nav_urge), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Global Focus Rooms", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text("Co-work with humans worldwide", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Timer section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .scale(if (isFocusing) ringScale else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                if (isFocusing)
                                    listOf(EmeraldAccent.copy(alpha = 0.08f), Color.Transparent)
                                else
                                    listOf(Color.Transparent, Color.Transparent)
                            )
                        )
                        .border(
                            4.dp,
                            if (isFocusing) EmeraldAccent.copy(alpha = ringAlpha) else MaterialTheme.colorScheme.outline,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%02d:%02d", minutesRemaining, secondsRemaining),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isFocusing) {
                            Text(
                                text = "\uD83D\uDFE2 Deep Work Active",
                                color = EmeraldAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "25-Minute Sprint",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Controls row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Rain toggle
                    OutlinedButton(
                        onClick = {
                            HapticFeedback.triggerClick(context)
                            if (isRainPlaying) {
                                SoundSynthesizer.stopAmbientRain()
                                isRainPlaying = false
                            } else {
                                SoundSynthesizer.startAmbientRain()
                                isRainPlaying = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isRainPlaying) CyanAccent else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            if (isRainPlaying) "\uD83C\uDF27\uFE0F Rain On" else "\uD83C\uDF27\uFE0F Rain",
                            color = if (isRainPlaying) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Focus start/stop
                    Button(
                        onClick = {
                            isFocusing = !isFocusing
                            HapticFeedback.triggerClick(context)
                            if (!isFocusing) {
                                FocusNotificationManager.cancelFocusNotification(context)
                                SoundSynthesizer.playWarning()
                            } else {
                                minutesRemaining = 25
                                secondsRemaining = 0
                                SoundSynthesizer.playSingingBowlChime(432f, 2.5f)
                            }
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFocusing) RoseDanger else EmeraldAccent
                        )
                    ) {
                        Text(
                            if (isFocusing) "\u23F9 End Session" else "\u25B6\uFE0F Start Sprint",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Peers section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    ),
                    RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Animated green dot
                val dotPulse = rememberInfiniteTransition(label = "dot")
                val dotScale by dotPulse.animateFloat(
                    initialValue = 0.8f, targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                    label = "dotScale"
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .scale(dotScale)
                        .clip(CircleShape)
                        .background(EmeraldAccent)
                )
                Text(
                    text = "1,204 PEERS FOCUSING NOW",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(peers) { peer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(peer.flag, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(peer.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(peer.task, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldAccent.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("${peer.minutesActive}m", color = EmeraldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
