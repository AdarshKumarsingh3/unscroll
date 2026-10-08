package com.unscroll.app.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import com.unscroll.app.data.FocusPeer
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.FocusNotificationManager
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun FocusRoomsScreen() {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }

    var isRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(25 * 60) }
    var userTask by remember { mutableStateOf("Reviewing proposal & writing Kotlin app") }
    var cheersCount by remember { mutableIntStateOf(148) }

    val formattedTime = remember(secondsLeft) {
        val m = secondsLeft / 60
        val s = secondsLeft % 60
        "%02d:%02d".format(m, s)
    }

    val peers = remember {
        listOf(
            FocusPeer("1", "Sarah K.", "🇺🇸", "Writing psychology thesis chapter", 18),
            FocusPeer("2", "Marcus L.", "🇩🇪", "Mobile UI system refactor in Jetpack Compose", 22),
            FocusPeer("3", "Priya N.", "🇮🇳", "Studying biochem metabolic pathways", 12),
            FocusPeer("4", "Kenji T.", "🇯🇵", "Async Kotlin coroutines architecture", 24)
        )
    }

    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft--

            // Update Android ongoing sticky notification
            FocusNotificationManager.showOngoingFocusNotification(context, formattedTime, userTask)

            if (secondsLeft == 0) {
                isRunning = false
                prefs.addReclaimedTime(0.42f) // 25 mins
                SoundSynthesizer.playSingingBowlChime(528f, 4f)
                FocusNotificationManager.cancelFocusNotification(context)
                FocusNotificationManager.showStreakCelebration(context, prefs.streakDays, prefs.hoursReclaimed)
            }
        } else if (!isRunning) {
            FocusNotificationManager.cancelFocusNotification(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                text = "👥 Global Room 01 • 318 Focusers Live",
                color = TealLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Timer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "DEEP FLOW BLOCK",
                    color = TealBright,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Circular Timer Display
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(CardDark)
                        .border(3.dp, TealPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formattedTime,
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Controls
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            isRunning = !isRunning
                            if (isRunning) {
                                SoundSynthesizer.playSingingBowlChime(440f, 2f)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealBright)
                    ) {
                        Icon(if (isRunning) Icons.Default.Close else Icons.Default.PlayArrow, contentDescription = null, tint = BgDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRunning) "Pause" else "Start 25m Focus", color = BgDark, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = {
                            isRunning = false
                            secondsLeft = 25 * 60
                            FocusNotificationManager.cancelFocusNotification(context)
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary)
                    }

                    IconButton(
                        onClick = { cheersCount++ }
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = RoseDanger)
                    }
                }

                Text(
                    text = "Notification shade displays real-time timer when phone is locked.",
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }
        }

        // Intention Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("YOUR COMMITTED INTENTION IN THIS ROOM:", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(userTask, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Live Peers List
        Text(
            text = "PEERS FOCUSING ALONGSIDE YOU",
            color = TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(peers) { peer ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(peer.flag, fontSize = 20.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(peer.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(peer.task, color = TextSecondary, fontSize = 11.sp)
                        }
                        Text("${peer.minutesActive}m", color = TealLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
