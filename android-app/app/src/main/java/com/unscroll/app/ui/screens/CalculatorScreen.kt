package com.unscroll.app.ui.screens

import android.content.Context
import android.content.Intent
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
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Viral Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(TealPrimary.copy(alpha = 0.15f))
                .border(1.dp, TealPrimary.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = " Top-of-Funnel Viral Engine  30%+ Share Rate",
                color = TealLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Screen-Time Cost Calculator",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            text = "See how many full 24-hour days you surrender to algorithms every year.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        // Interactive Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Feed Screen Time",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", dailyHours)} hrs/day",
                        color = TealLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = dailyHours,
                    onValueChange = { dailyHours = it },
                    valueRange = 0.5f..8.0f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = TealBright,
                        activeTrackColor = TealBright,
                        inactiveTrackColor = BorderDark
                    )
                )

                // Impact Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        title = "24h Days / Yr",
                        value = "${String.format(Locale.US, "%.1f", fullDaysPerYear)} d",
                        subtitle = "Full 24-hr days lost",
                        accentColor = RoseDanger,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Waking Days",
                        value = "${String.format(Locale.US, "%.1f", wakingDaysPerYear)} d",
                        subtitle = "Equivalent 16h days",
                        accentColor = AmberAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Over 30 Yrs",
                        value = "${String.format(Locale.US, "%.1f", lifetime30Years)} yrs",
                        subtitle = "Of pure life stolen",
                        accentColor = PurpleAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(color = BorderDark)

                Text(
                    text = "WHAT THAT TIME COULD HAVE BOUGHT YOU:",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OpportunityItem(
                        iconRes = R.drawable.ic_nav_calculator,
                        title = "$booksPerYear Books",
                        subtitle = "Read cover to cover",
                        color = EmeraldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    OpportunityItem(
                        iconRes = R.drawable.ic_nav_replacements,
                        title = "$workoutsPerYear Gym Sprints",
                        subtitle = "1-hour sessions",
                        color = TealBright,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Share Card Preview with Android Share Intent
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                CardDark,
                                TealPrimary.copy(alpha = 0.12f)
                            )
                        )
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = " SHAREABLE RESULT CARD",
                    color = TealLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "At ${String.format(Locale.US, "%.1f", dailyHours)}h/day, I lose ${String.format(Locale.US, "%.1f", fullDaysPerYear)} full days every year to doomscrolling. That's $booksPerYear books stolen by algorithms.",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                )

                Button(
                    onClick = {
                        shareScreenTimeReport(context, dailyHours, fullDaysPerYear, booksPerYear)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(painter = painterResource(R.drawable.ic_nav_pitch), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Result to WhatsApp / Instagram", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick CTAs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToUrgeSurfer,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary)
            ) {
                Text("Try 90s Urge Surfer")
            }

            Button(
                onClick = onNavigateToReplacements,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark)
            ) {
                Text("Browse Micro-Bites")
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
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.1f))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(title, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextTertiary, fontSize = 9.sp, lineHeight = 11.sp)
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
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(painter = painterResource(iconRes), contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Column {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextTertiary, fontSize = 10.sp)
        }
    }
}

private fun shareScreenTimeReport(context: Context, dailyHours: Float, daysLost: Float, books: Int) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                " I just audited my screen-time on Unscroll: At ${String.format(Locale.US, "%.1f", dailyHours)}h/day, I lose ${String.format(Locale.US, "%.1f", daysLost)} full 24-hr days every year to short-form feeds! That's $books books stolen. Reclaim your attention with Unscroll: The Anti-Doomscroll Habit OS."
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Screen-Time Cost")
        context.startActivity(shareIntent)
    } catch (_: Exception) {
    }
}

