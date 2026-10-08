package com.unscroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.theme.*

@Composable
fun FounderDeckScreen() {
    var selectedScenario by remember { mutableStateOf("BASE") }

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
                .background(AmberAccent.copy(alpha = 0.15f))
                .border(1.dp, AmberAccent.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "💼 Prepared for the Founders & Executive Team",
                color = AmberAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "90-Day Plan & Business Proposal",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            text = "Our defensible moat: Friction + Replacement + Community.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        // The 3-Pillar Architecture
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PillarCard("1. Friction", "5s breath pause stops automatic loop", TealBright, Modifier.weight(1f))
            PillarCard("2. Replace", "30-90s dopamine bites satisfy brain", AmberAccent, Modifier.weight(1f))
            PillarCard("3. Social", "Focus rooms & streaks build moat", PurpleAccent, Modifier.weight(1f))
        }

        // 90-Day Execution Roadmap
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Timeline, contentDescription = null, tint = TealBright)
                    Text("THE 90-DAY EXECUTION MODEL", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                PhaseItem(
                    phase = "Phase 1: Foundation (Days 1–30)",
                    goal = "Viral Cost Calculator, Urge Surfer, landing page.",
                    gate = "Gate: 500+ completions / waitlist signups."
                )

                PhaseItem(
                    phase = "Phase 2: Retention Core (Days 31–60)",
                    goal = "Android Interceptor, 30+ activity bites, user dashboard.",
                    gate = "Gate: Day-7 Retention ≥ 25%."
                )

                PhaseItem(
                    phase = "Phase 3: Community & Scale (Days 61–90)",
                    goal = "Live Focus Rooms, Stripe/Play billing, 2-3 B2B pilots.",
                    gate = "Gate: 5k-10k registered users, first paying subscribers."
                )
            }
        }

        // Year 1 Revenue Scenarios
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = AmberAccent)
                    Text("YEAR 1 REVENUE MODELS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("CONSERVATIVE", "BASE", "OPTIMISTIC").forEach { sc ->
                        FilterChip(
                            selected = selectedScenario == sc,
                            onClick = { selectedScenario = sc },
                            label = { Text(sc, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberAccent,
                                selectedLabelColor = BgDark
                            )
                        )
                    }
                }

                when (selectedScenario) {
                    "CONSERVATIVE" -> RevenueDetails("50,000", "3%", "1,500", "$45,000", "$30,000", "$75,000")
                    "BASE" -> RevenueDetails("150,000", "4%", "6,000", "$180,000", "$60,000", "$240,000")
                    else -> RevenueDetails("400,000", "5%", "20,000", "$600,000", "$120,000", "$720,000")
                }
            }
        }

        // Decisions Needed from Founders
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = EmeraldAccent)
                    Text("ACTION ITEMS NEEDED FROM FOUNDER", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Text("• Approve 90-day plan and focus on doomscrolling first.", color = TextSecondary, fontSize = 12.sp)
                Text("• Confirm budget, engineering team & weekly time commitment.", color = TextSecondary, fontSize = 12.sp)
                Text("• Authorize outreach to 2 to 3 pilot institutions ($12/seat/yr).", color = TextSecondary, fontSize = 12.sp)
                Text("• Agree on Phase gates (waitlist size & D-7 retention).", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PillarCard(title: String, desc: String, accent: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(title, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = TextSecondary, fontSize = 9.sp, lineHeight = 12.sp)
        }
    }
}

@Composable
private fun PhaseItem(phase: String, goal: String, gate: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardDark)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(phase, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(goal, color = TextSecondary, fontSize = 11.sp)
        Text(gate, color = TealLight, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RevenueDetails(
    users: String,
    conv: String,
    subs: String,
    subRev: String,
    b2bRev: String,
    total: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Registered Users:", color = TextSecondary, fontSize = 12.sp)
            Text(users, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Paid Conversion:", color = TextSecondary, fontSize = 12.sp)
            Text("$conv ($subs subs)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("B2C Subscription ARR:", color = TextSecondary, fontSize = 12.sp)
            Text(subRev, color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Institutional B2B ARR:", color = TextSecondary, fontSize = 12.sp)
            Text(b2bRev, color = TealLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Divider(color = BorderDark)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("TOTAL PROJECTED ARR:", color = EmeraldAccent, fontSize = 13.sp, fontWeight = FontWeight.Black)
            Text(total, color = EmeraldAccent, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}
