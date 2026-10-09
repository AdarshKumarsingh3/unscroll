package com.unscroll.app.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.R
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import java.util.Locale

@Composable
fun CalculatorScreen(
    onNavigateToUrgeSurfer: () -> Unit,
    onNavigateToReplacements: () -> Unit
) {
    val context = LocalContext.current
    var dailyHours by remember { mutableFloatStateOf(2.5f) }

    val yearlyHours = (dailyHours * 365).toInt()
    val fullDaysPerYear = (yearlyHours / 24f)
    val wakingDaysPerYear = (yearlyHours / 16f)
    val booksPerYear = (yearlyHours / 6.5f).toInt()
    val workoutsPerYear = yearlyHours
    val lifetime30Years = (dailyHours * 365 * 30 / (24 * 365))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Viral Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(TealPrimary.copy(alpha = 0.15f))
                .border(1.dp, TealPrimary.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "⚡ The Viral Cost Calculator",
                color = TealBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Time is Zero-Sum",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "See exactly how much life you surrender to algorithms every single year.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Interactive Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Screen Time",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " hrs",
                        color = TealBright,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Slider(
                    value = dailyHours,
                    onValueChange = { 
                        dailyHours = it 
                        HapticFeedback.triggerClick(context)
                    },
                    valueRange = 0.5f..8.0f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = TealBright,
                        activeTrackColor = TealBright,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Impact Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "24h DAYS",
                        value = " d",
                        subtitle = "Full days lost / yr",
                        accentColor = RoseDanger,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "WAKING DAYS",
                        value = " d",
                        subtitle = "16h days lost / yr",
                        accentColor = AmberAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PurpleAccent.copy(alpha=0.1f))
                        .border(1.dp, PurpleAccent.copy(alpha=0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier=Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("OVER 30 YEARS", color = PurpleAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Of pure life stolen", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                        Text(" yrs", color = MaterialTheme.colorScheme.onSurface, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Text(
                    text = "WHAT THAT TIME COULD HAVE BOUGHT YOU:",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OpportunityItem(
                        iconRes = R.drawable.ic_nav_calculator,
                        title = " Books",
                        subtitle = "Cover to cover",
                        color = EmeraldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    OpportunityItem(
                        iconRes = R.drawable.ic_nav_replacements,
                        title = " Workouts",
                        subtitle = "1-hour sessions",
                        color = BlueCalm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Share Card Preview with Android Share Intent
        AnimatedPressCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { shareScreenTimeReport(context, dailyHours, fullDaysPerYear, booksPerYear) },
            playClickSound = true
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    TealPrimary.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📢 TAP TO SHARE THIS REALITY CHECK",
                        color = TealBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "At h/day, I lose  full days every year to doomscrolling. That's  books stolen by algorithms.",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Quick CTAs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToUrgeSurfer,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealBright),
                border = androidx.compose.foundation.BorderStroke(2.dp, TealPrimary)
            ) {
                Text("Try 90s Surfer", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNavigateToReplacements,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text("Dopamine Bites", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(accentColor.copy(alpha = 0.1f))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(title, color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing=0.5.sp)
            Spacer(modifier=Modifier.height(4.dp))
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(modifier=Modifier.height(2.dp))
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun OpportunityItem(
    iconRes: Int,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(painter = painterResource(iconRes), contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Column {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

private fun shareScreenTimeReport(context: Context, dailyHours: Float, daysLost: Float, books: Int) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "📱 I just audited my screen-time on Unscroll: At h/day, I lose  full 24-hr days every year to short-form feeds! That's  books stolen. Reclaim your attention with Unscroll: The Anti-Doomscroll Habit OS."
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Screen-Time Cost")
        context.startActivity(shareIntent)
    } catch (_: Exception) {
    }
}
