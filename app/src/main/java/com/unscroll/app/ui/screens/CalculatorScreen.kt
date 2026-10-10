package com.unscroll.app.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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
import java.util.Locale

@Composable
fun CalculatorScreen(
    onNavigateToUrgeSurfer: () -> Unit,
    onNavigateToReplacements: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    var dailyHours by remember { mutableFloatStateOf(prefs.dailyScreenTimeSetting) }

    val yearlyHours = (dailyHours * 365).toInt()
    val fullDaysPerYear = yearlyHours / 24f
    val wakingDaysPerYear = yearlyHours / 16f
    val booksPerYear = (yearlyHours / 6.5f).toInt()
    val workoutsPerYear = yearlyHours
    val lifetime30Years = dailyHours * 365 * 30 / (24 * 365)
    val moviesPerYear = (yearlyHours / 2f).toInt()
    val languageLessons = (yearlyHours / 0.5f).toInt()

    // Danger level animation
    val dangerLevel = ((dailyHours - 0.5f) / 7.5f).coerceIn(0f, 1f)
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseAnim.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulseAlpha"
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
        // Hero gradient badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.horizontalGradient(listOf(RoseDanger.copy(alpha = 0.15f), OrangeAccent.copy(alpha = 0.15f)))
                )
                .border(1.dp, RoseDanger.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "\u26A1 The Viral Cost Calculator",
                color = RoseDanger,
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
            textAlign = TextAlign.Center
        )

        // MAIN INTERACTIVE SLIDER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
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
                    // Big animated number
                    Text(
                        text = String.format(Locale.US, "%.1f hrs", dailyHours),
                        color = if (dailyHours > 4f) RoseDanger else if (dailyHours > 2f) AmberAccent else EmeraldAccent,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Slider(
                    value = dailyHours,
                    onValueChange = {
                        dailyHours = it
                        prefs.dailyScreenTimeSetting = it
                        HapticFeedback.triggerClick(context)
                    },
                    valueRange = 0.5f..8.0f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = if (dailyHours > 4f) RoseDanger else if (dailyHours > 2f) AmberAccent else TealBright,
                        activeTrackColor = if (dailyHours > 4f) RoseDanger else if (dailyHours > 2f) AmberAccent else TealBright,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline
                    )
                )

                // IMPACT METRICS - 2x2 grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "24h DAYS LOST",
                        value = String.format(Locale.US, "%.0f", fullDaysPerYear),
                        subtitle = "Full days / year",
                        accentColor = RoseDanger,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "WAKING DAYS",
                        value = String.format(Locale.US, "%.0f", wakingDaysPerYear),
                        subtitle = "16h days / year",
                        accentColor = OrangeAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "YEARLY HOURS",
                        value = "$yearlyHours",
                        subtitle = "Total hrs scrolling",
                        accentColor = PurpleAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "WORKOUTS",
                        value = "$workoutsPerYear",
                        subtitle = "1-hr sessions lost",
                        accentColor = BlueCalm,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Lifetime horror stat
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    RoseDanger.copy(alpha = pulseAlpha * dangerLevel),
                                    PurpleAccent.copy(alpha = pulseAlpha * dangerLevel * 0.6f)
                                )
                            )
                        )
                        .border(1.dp, RoseDanger.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("\uD83D\uDC80 OVER 30 YEARS", color = RoseLight, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            Text("Of pure life stolen", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                        Text(
                            text = String.format(Locale.US, "%.1f yrs", lifetime30Years),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                // Opportunity cost section
                Text(
                    text = "WHAT THAT TIME COULD BUY YOU:",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OpportunityChip(
                        emoji = "\uD83D\uDCDA",
                        value = "$booksPerYear",
                        label = "Books",
                        color = EmeraldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    OpportunityChip(
                        emoji = "\uD83C\uDFAC",
                        value = "$moviesPerYear",
                        label = "Movies",
                        color = PurpleAccent,
                        modifier = Modifier.weight(1f)
                    )
                    OpportunityChip(
                        emoji = "\uD83C\uDF0D",
                        value = "$languageLessons",
                        label = "Lessons",
                        color = BlueCalm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // SHARE BUTTON
        AnimatedPressCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                SoundSynthesizer.playWhoosh()
                shareScreenTimeReport(context, dailyHours, fullDaysPerYear, booksPerYear)
            },
            playClickSound = false
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(listOf(GradientTealStart, GradientBlueEnd))
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "\uD83D\uDCE2 SHARE THIS REALITY CHECK",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "At ${String.format(Locale.US, "%.1f", dailyHours)}h/day, I lose ${String.format(Locale.US, "%.0f", fullDaysPerYear)} full days every year. That's $booksPerYear books stolen by algorithms.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Navigation CTAs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    SoundSynthesizer.playWhoosh()
                    onNavigateToUrgeSurfer()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealBright),
                border = androidx.compose.foundation.BorderStroke(2.dp, TealPrimary)
            ) {
                Text("\uD83C\uDF0A 90s Surfer", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    SoundSynthesizer.playWhoosh()
                    onNavigateToReplacements()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
            ) {
                Text("\u2728 Bites", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
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
            .background(accentColor.copy(alpha = 0.08f))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(title, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun OpportunityChip(
    emoji: String,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun shareScreenTimeReport(context: Context, dailyHours: Float, daysLost: Float, books: Int) {
    try {
        val text = "\uD83D\uDCF1 I just audited my screen-time on Unscroll: At ${String.format(Locale.US, "%.1f", dailyHours)}h/day, I lose ${String.format(Locale.US, "%.0f", daysLost)} full 24-hr days every year to short-form feeds! That's $books books stolen. Reclaim your attention: The Anti-Doomscroll Habit OS."
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Screen-Time Cost"))
    } catch (_: Exception) {
    }
}
