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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

    // Dummy data for global peers
    val peers = remember {
        listOf(
            FocusPeer("1", "Elena", "🇪🇸", "Studying for Med Boards", 14),
            FocusPeer("2", "Kenji", "🇯🇵", "Coding Frontend", 42),
            FocusPeer("3", "Sarah", "🇺🇸", "Writing Chapter 3", 8),
            FocusPeer("4", "David", "🇬🇧", "Deep Work", 112)
        )
    }

    // Timer pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
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
                    SoundSynthesizer.playSingingBowlChime(432f, 4.0f)
                    prefs.addReclaimedTime(25f / 60f)
                    FocusNotificationManager.cancelFocusNotification(context)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                HapticFeedback.triggerClick(context)
                onNavigateBack() 
            }) {
                Icon(painter = painterResource(R.drawable.ic_nav_urge), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Global Focus Rooms", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        }

        // Timer Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(CircleShape)
                        .border(
                            4.dp,
                            if (isFocusing) EmeraldAccent.copy(alpha = alpha) else MaterialTheme.colorScheme.outline,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%02d:%02d", minutesRemaining, secondsRemaining),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isFocusing) {
                            Text(
                                text = "Deep Work Active",
                                color = EmeraldAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                AnimatedPressCard(
                    onClick = {
                        isFocusing = !isFocusing
                        HapticFeedback.triggerClick(context)
                        if (!isFocusing) {
                            FocusNotificationManager.cancelFocusNotification(context)
                        } else {
                            SoundSynthesizer.playSingingBowlChime(432f, 2f)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isFocusing) RoseDanger else MaterialTheme.colorScheme.onBackground)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFocusing) "ABORT MISSION" else "START 25m SPRINT",
                            color = if (isFocusing) Color.White else MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        // Peers Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(EmeraldAccent))
                Text(
                    text = "1,204 PEERS FOCUSING RIGHT NOW",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(peers) { peer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(peer.flag, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(peer.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(peer.task, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        }
                        Text("m", color = EmeraldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

