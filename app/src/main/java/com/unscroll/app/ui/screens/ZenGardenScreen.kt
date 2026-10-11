package com.unscroll.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class GardenPlot(
    val stage: Int, // 0 = Empty, 1 = Seed, 2 = Sprout, 3 = Bloomed
    val plantIndex: Int
)

private val PLANT_EMOJIS = listOf(
    "🌻", // Sunflower
    "🌸", // Cherry Blossom
    "🌲", // Evergreen
    "🌴", // Palm
    "🌷", // Tulip
    "🌵", // Cactus
    "🌺", // Hibiscus
    "🌹", // Rose
    "🌼", // Daisy
    "🍀", // Lucky Clover
    "🎋", // Bamboo
    "🍄", // Zen Mushroom
    "🪴", // Potted Herb
    "🪻", // Lavender
    "🌾", // Golden Wheat
    "🍁"  // Maple
)

private val PLANT_NAMES = listOf(
    "Sunflower",
    "Blossom",
    "Evergreen",
    "Palm Tree",
    "Tulip",
    "Cactus",
    "Hibiscus",
    "Rose",
    "Daisy",
    "Clover",
    "Bamboo",
    "Mushroom",
    "Houseplant",
    "Lavender",
    "Wheat",
    "Maple"
)

@Composable
fun ZenGardenScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    // 4x4 Grid plots (16 total)
    val plots = remember {
        mutableStateListOf<GardenPlot>().apply {
            repeat(16) {
                add(GardenPlot(stage = 0, plantIndex = (0 until PLANT_EMOJIS.size).random()))
            }
        }
    }

    var isRainPlaying by remember { mutableStateOf(false) }
    var secondsGardening by remember { mutableIntStateOf(0) }

    // Gardening timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsGardening++
        }
    }

    // Stop ambient rain when navigating away
    DisposableEffect(Unit) {
        onDispose {
            if (isRainPlaying) {
                SoundSynthesizer.stopAmbientRain()
            }
        }
    }

    val plantsGrown = plots.count { it.stage == 3 }
    val gardenLevel = (plantsGrown / 4) + 1

    fun plantOrTend(index: Int) {
        val currentPlot = plots[index]
        if (currentPlot.stage == 0) {
            // Stage 1: Seed
            plots[index] = currentPlot.copy(stage = 1)
            SoundSynthesizer.playTick()
            HapticFeedback.triggerClick(context)

            coroutineScope.launch {
                // Stage 2: Sprout after brief growth delay
                delay(1200)
                if (plots[index].stage == 1) {
                    plots[index] = plots[index].copy(stage = 2)
                    SoundSynthesizer.playTick()
                    HapticFeedback.triggerClick(context)
                }

                // Stage 3: Fully Bloomed after finishing growth
                delay(1500)
                if (plots[index].stage == 2) {
                    plots[index] = plots[index].copy(stage = 3)
                    SoundSynthesizer.playSingingBowlChime(432f, 2.5f)
                    HapticFeedback.triggerSuccess(context)
                    prefs.addReclaimedTime(0.25f / 60f)
                }
            }
        } else if (currentPlot.stage == 3) {
            // Meditative chime when interacting with mature plant
            SoundSynthesizer.playSingingBowlChime(528f, 2f)
            HapticFeedback.triggerClick(context)
        }
    }

    fun resetGarden() {
        SoundSynthesizer.playWhoosh()
        HapticFeedback.triggerClick(context)
        for (i in 0 until 16) {
            plots[i] = GardenPlot(stage = 0, plantIndex = (PLANT_EMOJIS.indices).random())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation & Rain Control Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    HapticFeedback.triggerClick(context)
                    if (isRainPlaying) {
                        SoundSynthesizer.stopAmbientRain()
                        isRainPlaying = false
                    }
                    onNavigateBack()
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CardDark)
                    .border(1.dp, BorderDark, CircleShape)
            ) {
                Text(
                    text = "←",
                    color = TextPrimaryDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Ambient Rain Button
            AnimatedPressCard(
                onClick = {
                    HapticFeedback.triggerClick(context)
                    if (isRainPlaying) {
                        SoundSynthesizer.stopAmbientRain()
                        isRainPlaying = false
                    } else {
                        SoundSynthesizer.startAmbientRain()
                        isRainPlaying = true
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isRainPlaying)
                                Brush.horizontalGradient(listOf(BlueCalm.copy(alpha = 0.35f), CyanAccent.copy(alpha = 0.35f)))
                            else
                                Brush.horizontalGradient(listOf(CardDark, CardDark))
                        )
                        .border(
                            1.dp,
                            if (isRainPlaying) CyanAccent else BorderDark,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isRainPlaying) "🌧️" else "☁️",
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isRainPlaying) "Rain Playing" else "Zen Rain",
                            color = if (isRainPlaying) CyanAccent else TextSecondaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Gradient Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            GradientTealStart.copy(alpha = 0.22f),
                            GradientPurpleStart.copy(alpha = 0.12f),
                            CardDark
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(TealBright.copy(alpha = 0.5f), PurpleAccent.copy(alpha = 0.3f))),
                    RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.2f))
                        .border(1.dp, TealLight.copy(alpha = 0.4f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🌱 Anti-Scroll Sanctuary",
                        color = TealLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Zen Garden",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )

                Text(
                    text = "Grow peaceful life instead of scrolling feeds. Tap plots to sow seeds and watch your sanctuary bloom.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        // Stats Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderDark, BorderDark)))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GardenStatItem(
                        title = "Plants Grown",
                        value = "${plantsGrown}/16",
                        emoji = "🌸",
                        tint = EmeraldAccent
                    )
                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = BorderDark
                    )
                    GardenStatItem(
                        title = "Garden Level",
                        value = "Lvl $gardenLevel",
                        emoji = "⭐",
                        tint = AmberAccent
                    )
                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = BorderDark
                    )
                    val minutes = secondsGardening / 60
                    val seconds = secondsGardening % 60
                    GardenStatItem(
                        title = "Zen Time",
                        value = String.format(Locale.US, "%02d:%02d", minutes, seconds),
                        emoji = "⏳",
                        tint = CyanAccent
                    )
                }

                // Progress towards full garden bloom
                val progressFloat = (plantsGrown / 16f).coerceIn(0f, 1f)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (plantsGrown == 16) "Sanctuary fully flourishing!" else "Garden Bloom Progress",
                            fontSize = 11.sp,
                            color = TextSecondaryDark,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(progressFloat * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = TealBright,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = progressFloat,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TealBright,
                        trackColor = BorderDark,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // 4x4 Garden Grid (16 Plots)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 4) {
                        val index = row * 4 + col
                        val plot = plots[index]
                        ZenPlotCard(
                            plot = plot,
                            modifier = Modifier.weight(1f),
                            onClick = { plantOrTend(index) }
                        )
                    }
                }
            }
        }

        // Bottom Controls: Reset Garden Button & Zen Reflection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedPressCard(
                onClick = { resetGarden() },
                playClickSound = true
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CardDark, Color(0xFF222B3D))
                            )
                        )
                        .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🔄", fontSize = 14.sp)
                        Text(
                            text = "Reset Garden",
                            color = TextPrimaryDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Zen Reflection Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark.copy(alpha = 0.6f))
                .border(1.dp, BorderDark.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "“To plant a garden is to believe in tomorrow.”\nEvery seed sown here is a minute reclaimed from scrolling.",
                color = TextTertiaryDark,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun ZenPlotCard(
    plot: GardenPlot,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Spring bounce animation on growth stage change
    val scale = remember { Animatable(1f) }

    LaunchedEffect(plot.stage) {
        if (plot.stage > 0) {
            scale.snapTo(0.15f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            scale.snapTo(1f)
        }
    }

    // Dynamic gradient colors per stage
    val (bgGradient, borderColor) = when (plot.stage) {
        0 -> listOf(CardDark, Color(0xFF131A26)) to BorderDark.copy(alpha = 0.5f)
        1 -> listOf(Color(0xFF2E2215), Color(0xFF1A130B)) to AmberWarm.copy(alpha = 0.6f)
        2 -> listOf(Color(0xFF0F2D20), Color(0xFF0A1C14)) to EmeraldAccent.copy(alpha = 0.7f)
        else -> listOf(
            GradientTealStart.copy(alpha = 0.35f),
            GradientPurpleEnd.copy(alpha = 0.35f)
        ) to TealBright
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(bgGradient))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (plot.stage) {
                0 -> {
                    // Empty Soil Bed
                    Text(
                        text = "·",
                        fontSize = 22.sp,
                        color = TextTertiaryDark.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "tap",
                        fontSize = 9.sp,
                        color = TextTertiaryDark.copy(alpha = 0.5f)
                    )
                }
                1 -> {
                    // Seed Stage
                    Text(
                        text = "🌰",
                        fontSize = 24.sp,
                        modifier = Modifier.scale(scale.value)
                    )
                    Text(
                        text = "Seed",
                        fontSize = 9.sp,
                        color = AmberAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                2 -> {
                    // Sprout Stage
                    Text(
                        text = "🌿",
                        fontSize = 24.sp,
                        modifier = Modifier.scale(scale.value)
                    )
                    Text(
                        text = "Sprout",
                        fontSize = 9.sp,
                        color = EmeraldAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                else -> {
                    // Flower / Tree Bloomed
                    val plantEmoji = PLANT_EMOJIS[plot.plantIndex % PLANT_EMOJIS.size]
                    val plantName = PLANT_NAMES[plot.plantIndex % PLANT_NAMES.size]
                    Text(
                        text = plantEmoji,
                        fontSize = 26.sp,
                        modifier = Modifier.scale(scale.value)
                    )
                    Text(
                        text = plantName,
                        fontSize = 8.sp,
                        color = TealLight,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun GardenStatItem(
    title: String,
    value: String,
    emoji: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 12.sp)
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = tint
            )
        }
        Text(
            text = title,
            fontSize = 10.sp,
            color = TextSecondaryDark,
            fontWeight = FontWeight.Medium
        )
    }
}
