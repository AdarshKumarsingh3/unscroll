package com.unscroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.R
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.FocusNotificationManager
import com.unscroll.app.ui.theme.*
import java.util.Locale

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    var streak by remember { mutableIntStateOf(prefs.streakDays) }
    var hoursReclaimed by remember { mutableFloatStateOf(prefs.hoursReclaimed) }
    var urgesDefeated by remember { mutableIntStateOf(prefs.urgesDefeatedCount) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
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
                text = "📊 Visible Progress & Dopamine Rewiring Engine",
                color = TealLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Saved-Hours Wall",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        // KPI Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                title = "HOURS RECLAIMED",
                value = "${String.format(Locale.US, "%.1f", hoursReclaimed)}h",
                subtitle = "Life redirected from feeds",
                accentColor = TealBright,
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                title = "ACTIVE STREAK",
                value = "$streak d",
                subtitle = "Shield protected",
                accentColor = AmberAccent,
                modifier = Modifier.weight(1f)
            )
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    prefs.addReclaimedTime(0.25f)
                    hoursReclaimed = prefs.hoursReclaimed
                    urgesDefeated = prefs.urgesDefeatedCount
                    FocusNotificationManager.showStreakCelebration(context, streak, hoursReclaimed)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Icon(painter = painterResource(R.drawable.ic_nav_replacements), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Urge (+15m)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    FocusNotificationManager.showMindfulnessNudge(
                        context,
                        "Mindfulness Moment",
                        "You have reclaimed ${String.format(Locale.US, "%.1f", hoursReclaimed)} hours! What will you create today?"
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TealBright)
            ) {
                Icon(painter = painterResource(R.drawable.ic_notification), contentDescription = null, tint = TealBright, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Test Nudge", color = TealLight, fontSize = 11.sp)
            }
        }

        // Dopamine Rewiring Milestones
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
                    text = "DOPAMINE REWIRING ROADMAP",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                MilestoneItem("Day 1: Loop Awareness", "First unconscious open paused and redirected", true)
                MilestoneItem("Day 3: Craving Peak Master", "Successfully rode 3 Urge Surfers", true)
                MilestoneItem("Day 7: Dopamine Baseline Reset", "Brain receptors begin resensitizing", streak >= 7)
                MilestoneItem("Day 14: Deep Flow Sovereign", "Prefrontal cortex default control restored", streak >= 14)
                MilestoneItem("Day 30: Algorithm-Free Mind", "Zero automatic thumb compulsions", streak >= 30)
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = TextTertiary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun MilestoneItem(title: String, desc: String, achieved: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (achieved) CardDark else BgDark)
            .border(1.dp, if (achieved) EmeraldAccent.copy(alpha = 0.3f) else BorderDark, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (achieved) EmeraldAccent else BorderDark),
            contentAlignment = Alignment.Center
        ) {
            if (achieved) {
                Icon(painter = painterResource(R.drawable.ic_nav_interceptor), contentDescription = null, tint = BgDark, modifier = Modifier.size(16.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = if (achieved) Color.White else TextTertiary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = TextSecondary, fontSize = 10.sp)
        }
    }
}
