package com.unscroll.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.R
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*

@Composable
fun FounderDeckScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(TealPrimary.copy(alpha = 0.15f), PurpleAccent.copy(alpha = 0.05f), MaterialTheme.colorScheme.background)
                    )
                )
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Column {
                IconButton(onClick = { 
                    HapticFeedback.triggerClick(context)
                    onNavigateBack() 
                }, modifier = Modifier.offset(x = (-12).dp)) {
                    Icon(painter = painterResource(R.drawable.ic_nav_urge), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "UNSCROLL",
                    color = TealPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The Anti-Doomscroll\nHabit OS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "A systemic solution to short-form video addiction. Reclaiming 1 billion hours of human potential.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
            }
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Core Problem
            PitchSection(
                icon = "\uD83D\uDEA8",
                title = "THE PROBLEM",
                content = "Social media companies spend billions optimizing algorithms to bypass the prefrontal cortex. Users don't want to scroll for 4 hours \u2014 they are neurologically hijacked.",
                accentColor = RoseDanger
            )

            // The Solution
            PitchSection(
                icon = "\uD83D\uDCA1",
                title = "THE SOLUTION",
                content = "We insert friction precisely at the point of behavioral failure. A 5-second mindfulness pause before feeds open re-engages the prefrontal cortex.",
                accentColor = EmeraldAccent
            )

            // 3 Pillars
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(TealPrimary.copy(alpha = 0.08f), MaterialTheme.colorScheme.surface)
                            )
                        )
                        .border(1.dp, TealPrimary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("\uD83C\uDFDB\uFE0F THE 3 PILLARS", color = TealBright, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

                        PillarItem(number = "1", title = "Intercept", desc = "Accessibility service blocks unconscious app opens.", color = CyanAccent)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        PillarItem(number = "2", title = "Recalibrate", desc = "90-second Urge Surfer drops dopamine baseline.", color = IndigoAccent)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        PillarItem(number = "3", title = "Replace", desc = "30-second micro-activities satisfy the craving.", color = EmeraldAccent)
                    }
                }
            }

            // Monetization
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(PurpleAccent.copy(alpha = 0.08f), MaterialTheme.colorScheme.surface)
                            )
                        )
                        .border(1.dp, PurpleAccent.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("\uD83D\uDCB0 BUSINESS MODEL", color = PurpleAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

                        Text("Freemium SaaS", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Black)

                        PricingTier(emoji = "\uD83C\uDD93", name = "Free", desc = "Basic Interceptor, 1 Replacement", color = EmeraldAccent)
                        PricingTier(emoji = "\u2B50", name = "Pro \u2022 5/mo", desc = "Advanced Analytics, All Replacements, Focus Rooms", color = AmberAccent)
                        PricingTier(emoji = "\uD83D\uDC8E", name = "Annual \u2022 49/yr", desc = "Everything + Priority Features", color = PurpleAccent)
                    }
                }
            }

            // CTA
            AnimatedPressCard(
                onClick = {
                    SoundSynthesizer.playWhoosh()
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AdarshKumarsingh3/unscroll"))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(listOf(TealPrimary, CyanAccent))
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\uD83D\uDE80 Join the Waitlist / View Repo", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PitchSection(icon: String, title: String, content: String, accentColor: Color) {
    Column {
        Text("$icon $title", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(content, color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp, lineHeight = 26.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PillarItem(number: String, title: String, desc: String, color: Color) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(2.dp, color.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = color, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Column {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun PricingTier(emoji: String, name: String, desc: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.06f))
            .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}
