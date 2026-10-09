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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.R
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
                        listOf(TealPrimary.copy(alpha=0.2f), MaterialTheme.colorScheme.background)
                    )
                )
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The Anti-Doomscroll Habit OS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "A systemic solution to short-form video addiction. Reclaiming 1 billion hours of human potential.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Core Problem
            PitchSection(
                title = "THE PROBLEM",
                content = "Social media companies spend billions optimizing algorithms to bypass the prefrontal cortex. Users don't want to scroll for 4 hours, they are neurologically hijacked."
            )

            // The Solution
            PitchSection(
                title = "THE SOLUTION",
                content = "We insert friction precisely at the point of behavioral failure. By adding a 5-second mindfulness pause before feeds open, we re-engage the prefrontal cortex."
            )

            // 3 Pillars
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("THE 3 PILLARS", color = TealBright, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    
                    PillarItem(number = "1", title = "Intercept", desc = "Accessibility service blocks unconscious app opens.")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    PillarItem(number = "2", title = "Recalibrate", desc = "90-second Urge Surfer drops dopamine baseline.")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    PillarItem(number = "3", title = "Replace", desc = "30-second micro-activities satisfy the craving.")
                }
            }

            // Monetization
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha=0.3f))
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("BUSINESS MODEL", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    
                    Text("Freemium SaaS", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text(
                        "• Free: Basic Interceptor, 1 Replacement\n" +
                        "• \/mo: Advanced Analytics, All Replacements, Global Focus Rooms\n" +
                        "• \/yr: Annual Subscription",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                }
            }

            // CTA
            AnimatedPressCard(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AdarshKumarsingh3/unscroll"))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(TealPrimary)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Join the Waitlist / View Repo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PitchSection(title: String, content: String) {
    Column {
        Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(content, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PillarItem(number: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(TealPrimary.copy(alpha=0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = TealBright, fontWeight = FontWeight.Black)
        }
        Column {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}
