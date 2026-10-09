package com.unscroll.app.ui.screens

import androidx.compose.animation.core.*
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
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import java.util.Locale

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    
    var streak by remember { mutableIntStateOf(prefs.streakDays) }
    var hoursReclaimed by remember { mutableFloatStateOf(prefs.hoursReclaimed) }
    var urgesDefeated by remember { mutableIntStateOf(prefs.urgesDefeatedCount) }

    // Intro animation state
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimation = true }
    
    val animatedHours by animateFloatAsState(
        targetValue = if (startAnimation) hoursReclaimed else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "hours"
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
        // Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(TealPrimary.copy(alpha = 0.15f))
                .border(1.dp, TealPrimary.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "✨ Visible Progress Engine",
                color = TealBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Your Reclaimed Life",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // KPI Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                title = "HOURS SAVED",
                value = "h",
                subtitle = if (hoursReclaimed > 0) "Redirected from feeds" else "Start surfing urges!",
                accentColor = TealBright,
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                title = "ACTIVE STREAK",
                value = " d",
                subtitle = "Shield protected",
                accentColor = AmberAccent,
                modifier = Modifier.weight(1f)
            )
        }

        // Add 15 mins explicitly for testing dopamine hits
        AnimatedPressCard(
            onClick = {
                prefs.addReclaimedTime(0.25f)
                hoursReclaimed = prefs.hoursReclaimed
                urgesDefeated = prefs.urgesDefeatedCount
                streak = prefs.streakDays
                HapticFeedback.triggerSuccess(context)
                FocusNotificationManager.showStreakCelebration(context, streak, hoursReclaimed)
            },
            playClickSound = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(TealPrimary)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(R.drawable.ic_nav_replacements), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manual Add: Surfed an Urge (+15m)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Dopamine Rewiring Milestones
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "RECEPTOR REWIRING ROADMAP",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                MilestoneItem("Day 1: Loop Awareness", "First unconscious open paused", streak >= 1)
                MilestoneItem("Day 3: Craving Peak Master", "Riding the 90s wave", streak >= 3)
                MilestoneItem("Day 7: Baseline Reset", "Receptors resensitizing", streak >= 7)
                MilestoneItem("Day 14: Deep Flow", "Prefrontal cortex restored", streak >= 14)
                MilestoneItem("Day 30: Algorithm-Free", "Zero automatic thumb compulsions", streak >= 30)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun MilestoneItem(title: String, desc: String, achieved: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (achieved) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background)
            .border(1.dp, if (achieved) EmeraldAccent.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (achieved) EmeraldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            if (achieved) {
                Icon(painter = painterResource(R.drawable.ic_nav_interceptor), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = if (achieved) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}
