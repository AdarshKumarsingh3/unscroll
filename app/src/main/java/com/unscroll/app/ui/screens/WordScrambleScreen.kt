package com.unscroll.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.data.UnscrollPreferences
import com.unscroll.app.service.SoundSynthesizer
import com.unscroll.app.ui.components.AnimatedPressCard
import com.unscroll.app.ui.components.HapticFeedback
import com.unscroll.app.ui.theme.AmberAccent
import com.unscroll.app.ui.theme.AmberWarm
import com.unscroll.app.ui.theme.BgDark
import com.unscroll.app.ui.theme.BlueCalm
import com.unscroll.app.ui.theme.BorderDark
import com.unscroll.app.ui.theme.CardDark
import com.unscroll.app.ui.theme.CyanAccent
import com.unscroll.app.ui.theme.EmeraldAccent
import com.unscroll.app.ui.theme.IndigoAccent
import com.unscroll.app.ui.theme.PinkAccent
import com.unscroll.app.ui.theme.PurpleAccent
import com.unscroll.app.ui.theme.RoseDanger
import com.unscroll.app.ui.theme.SurfaceDark
import com.unscroll.app.ui.theme.TealBright
import com.unscroll.app.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class LetterTile(
    val id: Int,
    val char: Char
)

private data class WellnessWord(
    val word: String,
    val hint: String
)

private val WORD_POOL = listOf(
    WellnessWord("BREATHE", "Inhale peace, exhale tension"),
    WellnessWord("MINDFUL", "Fully aware in the current moment"),
    WellnessWord("PRESENT", "Here and now, beyond the screen"),
    WellnessWord("FOCUSED", "Undivided attention on what matters"),
    WellnessWord("CLARITY", "Mental stillness and clear thought"),
    WellnessWord("BALANCE", "Equilibrium between tech and living"),
    WellnessWord("SERENE", "Tranquil and undisturbed spirit"),
    WellnessWord("GRATITUDE", "Appreciating the abundance of now"),
    WellnessWord("FREEDOM", "Liberation from the endless scroll"),
    WellnessWord("PATIENCE", "Calm tolerance with the passing seconds"),
    WellnessWord("COURAGE", "Strength to set your phone aside"),
    WellnessWord("HARMONY", "Peaceful resonance within yourself"),
    WellnessWord("DETOX", "Cleansing your cognitive workspace"),
    WellnessWord("REWIRE", "Building healthier neural pathways"),
    WellnessWord("UNPLUG", "Disconnecting to truly reconnect"),
    WellnessWord("RECLAIM", "Taking back your precious hours"),
    WellnessWord("DIGITAL", "Conscious mastery over technology"),
    WellnessWord("SILENCE", "Deep sanctuary away from notification pings"),
    WellnessWord("NATURE", "The real world waiting right outside"),
    WellnessWord("MEDITATE", "Returning attention to stillness"),
    WellnessWord("PEACEFUL", "Quiet ease flowing through your day"),
    WellnessWord("STILLNESS", "Calm core untouched by digital noise"),
    WellnessWord("AWARENESS", "Awakened consciousness of your impulses"),
    WellnessWord("PURPOSE", "Living with deep, deliberate intention")
)

@Composable
fun WordScrambleScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UnscrollPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    // Game state
    var score by remember { mutableIntStateOf(0) }
    var wordsSolved by remember { mutableIntStateOf(0) }
    var hintsUsedTotal by remember { mutableIntStateOf(0) }
    var hintsUsedCurrentWord by remember { mutableIntStateOf(0) }
    var timeRemaining by remember { mutableIntStateOf(60) }
    var isPlaying by remember { mutableStateOf(true) }
    var showResults by remember { mutableStateOf(false) }

    // Shuffled word list for current session
    var availableWords by remember { mutableStateOf(WORD_POOL.shuffled()) }
    var wordIndex by remember { mutableIntStateOf(0) }

    val currentWellnessWord = remember(wordIndex, availableWords) {
        availableWords[wordIndex % availableWords.size]
    }
    val targetWord = currentWellnessWord.word

    // Tiles state
    var scrambledTiles by remember { mutableStateOf<List<LetterTile>>(emptyList()) }
    var answerTiles by remember { mutableStateOf<List<LetterTile>>(emptyList()) }
    var isWordSolvedInCurrentRound by remember { mutableStateOf(false) }

    // Animations
    val shakeOffset = remember { Animatable(0f) }
    var isShakeActive by remember { mutableStateOf(false) }

    // Scramble logic
    fun setupRound(word: String) {
        val indexedChars = word.mapIndexed { idx, ch -> LetterTile(id = idx, char = ch) }
        var scrambled = indexedChars.shuffled()
        var attempts = 0
        while (scrambled.map { it.char }.joinToString("") == word && attempts < 10) {
            scrambled = indexedChars.shuffled()
            attempts++
        }
        scrambledTiles = scrambled
        answerTiles = emptyList()
        hintsUsedCurrentWord = 0
        isWordSolvedInCurrentRound = false
    }

    // Initialize round
    LaunchedEffect(targetWord) {
        setupRound(targetWord)
    }

    // Timer countdown
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (timeRemaining > 0 && isPlaying) {
                delay(1000L)
                timeRemaining--
                if (timeRemaining == 0) {
                    isPlaying = false
                    showResults = true
                    SoundSynthesizer.playLevelUp()
                    HapticFeedback.triggerSuccess(context)
                    prefs.addReclaimedTime(1f / 60f)
                    prefs.recordBreathSession()
                }
            }
        }
    }

    // Wrong tap shake animation
    fun triggerWrongTapFeedback() {
        SoundSynthesizer.playWarning()
        HapticFeedback.triggerClick(context)
        isShakeActive = true
        coroutineScope.launch {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 360
                    0f at 0
                    (-14f) at 45
                    14f at 90
                    (-10f) at 150
                    10f at 210
                    (-6f) at 270
                    4f at 320
                    0f at 360
                }
            )
            isShakeActive = false
        }
    }

    // Next word advancement
    fun advanceToNextWord() {
        if (wordIndex + 1 < availableWords.size) {
            wordIndex++
        } else {
            availableWords = WORD_POOL.shuffled()
            wordIndex = 0
        }
    }

    // Letter tap handling
    fun onLetterTapped(tile: LetterTile) {
        if (!isPlaying || isWordSolvedInCurrentRound) return

        val nextExpectedChar = targetWord[answerTiles.size]
        if (tile.char == nextExpectedChar) {
            // Correct letter tapped!
            SoundSynthesizer.playTick()
            HapticFeedback.triggerClick(context)
            scrambledTiles = scrambledTiles.filter { it.id != tile.id }
            answerTiles = answerTiles + tile

            // Check if whole word completed
            if (answerTiles.size == targetWord.length) {
                isWordSolvedInCurrentRound = true
                score += 1
                wordsSolved += 1
                SoundSynthesizer.playSuccessChime()
                HapticFeedback.triggerSuccess(context)

                coroutineScope.launch {
                    delay(650L)
                    advanceToNextWord()
                }
            }
        } else {
            // Incorrect letter tapped
            triggerWrongTapFeedback()
        }
    }

    // Hint handling
    fun onHintClicked() {
        if (!isPlaying || isWordSolvedInCurrentRound) return
        if (hintsUsedCurrentWord >= 2 || answerTiles.size >= targetWord.length) return

        val neededChar = targetWord[answerTiles.size]
        val matchingTile = scrambledTiles.firstOrNull { it.char == neededChar }
        if (matchingTile != null) {
            hintsUsedCurrentWord++
            hintsUsedTotal++
            SoundSynthesizer.playTick()
            HapticFeedback.triggerClick(context)
            scrambledTiles = scrambledTiles.filter { it.id != matchingTile.id }
            answerTiles = answerTiles + matchingTile

            if (answerTiles.size == targetWord.length) {
                isWordSolvedInCurrentRound = true
                score += 1
                wordsSolved += 1
                SoundSynthesizer.playSuccessChime()
                HapticFeedback.triggerSuccess(context)

                coroutineScope.launch {
                    delay(650L)
                    advanceToNextWord()
                }
            }
        }
    }

    // Restart game
    fun restartGame() {
        score = 0
        wordsSolved = 0
        hintsUsedTotal = 0
        timeRemaining = 60
        showResults = false
        isPlaying = true
        availableWords = WORD_POOL.shuffled()
        wordIndex = 0
        setupRound(availableWords[0].word)
    }

    // Background ambient breathing subtle animation
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.03f,
        targetValue = 0.09f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BgDark,
                        IndigoAccent.copy(alpha = ambientPulse),
                        SurfaceDark
                    )
                )
            )
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    HapticFeedback.triggerClick(context)
                    onNavigateBack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "WORD SCRAMBLE",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Mindful Brain Exercise",
                    fontSize = 11.sp,
                    color = PurpleAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Shuffle available tiles button
            IconButton(
                onClick = {
                    if (isPlaying && scrambledTiles.isNotEmpty()) {
                        SoundSynthesizer.playTick()
                        HapticFeedback.triggerClick(context)
                        scrambledTiles = scrambledTiles.shuffled()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle Tiles",
                    tint = CyanAccent
                )
            }
        }

        Divider(
            color = BorderDark.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        if (showResults) {
            // Results Screen
            WordScrambleResults(
                score = score,
                wordsSolved = wordsSolved,
                hintsUsed = hintsUsedTotal,
                onPlayAgain = { restartGame() },
                onNavigateBack = onNavigateBack
            )
        } else {
            // Active Game Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Stats Bar Card
                GameStatsBar(
                    score = score,
                    wordsSolved = wordsSolved,
                    hintsUsed = hintsUsedTotal,
                    timeRemaining = timeRemaining
                )

                // Word Hint / Mindfulness Theme
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark.copy(alpha = 0.8f))
                        .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "THEME CLUE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TealBright
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentWellnessWord.hint,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Answer Slots Section (Top Row with Shake Animation)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "YOUR ANSWER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = if (isShakeActive) RoseDanger else TextSecondaryDark()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .offset(x = shakeOffset.value.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tileDimension = getResponsiveTileSize(targetWord.length)
                        val tileSpacing = if (targetWord.length > 7) 4.dp else 6.dp

                        for (i in 0 until targetWord.length) {
                            val placedTile = answerTiles.getOrNull(i)
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = tileSpacing / 2)
                                    .size(tileDimension)
                            ) {
                                if (placedTile != null) {
                                    // Animated scaled tile in answer row
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = scaleIn(
                                            animationSpec = spring(
                                                dampingRatio = 0.65f,
                                                stiffness = 400f
                                            )
                                        ) + fadeIn(),
                                        exit = scaleOut() + fadeOut()
                                    ) {
                                        GradientLetterTile(
                                            char = placedTile.char,
                                            sizeDp = tileDimension,
                                            isAnswerSlot = true,
                                            isSuccess = isWordSolvedInCurrentRound,
                                            onClick = { }
                                        )
                                    }
                                } else {
                                    // Empty placeholder slot
                                    Box(
                                        modifier = Modifier
                                            .size(tileDimension)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(CardDark.copy(alpha = 0.5f))
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isShakeActive) RoseDanger else BorderDark,
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(BorderDark)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrambled Letter Tiles Row
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TAP LETTERS IN ORDER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tileDimension = getResponsiveTileSize(targetWord.length)
                        val tileSpacing = if (targetWord.length > 7) 4.dp else 6.dp

                        scrambledTiles.forEach { tile ->
                            Box(
                                modifier = Modifier.padding(horizontal = tileSpacing / 2)
                            ) {
                                GradientLetterTile(
                                    char = tile.char,
                                    sizeDp = tileDimension,
                                    isAnswerSlot = false,
                                    isSuccess = false,
                                    onClick = { onLetterTapped(tile) }
                                )
                            }
                        }

                        if (scrambledTiles.isEmpty() && !isWordSolvedInCurrentRound) {
                            Text(
                                text = "All letters placed!",
                                color = TextSecondaryDark(),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Bar (Hint Button & Word Progress)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hint Button (max 2 per word)
                    val hintsRemaining = (2 - hintsUsedCurrentWord).coerceAtLeast(0)
                    AnimatedPressCard(
                        onClick = { onHintClicked() },
                        playClickSound = false,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (hintsRemaining > 0) AmberWarm.copy(alpha = 0.16f) else CardDark
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (hintsRemaining > 0) AmberAccent.copy(alpha = 0.5f) else BorderDark,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(vertical = 12.dp, horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Hint",
                                    tint = if (hintsRemaining > 0) AmberAccent else TextSecondaryDark(),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (hintsRemaining > 0) "Hint ($hintsRemaining left)" else "No hints left",
                                    color = if (hintsRemaining > 0) AmberAccent else TextSecondaryDark(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Reset Round Button
                    AnimatedPressCard(
                        onClick = {
                            if (isPlaying && !isWordSolvedInCurrentRound) {
                                SoundSynthesizer.playTick()
                                HapticFeedback.triggerClick(context)
                                setupRound(targetWord)
                            }
                        },
                        playClickSound = false,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CardDark)
                                .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                                .padding(vertical = 12.dp, horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset Round",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Reset Letters",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextSecondaryDark(): Color {
    return Color(0xFF94A3B8)
}

private fun getResponsiveTileSize(wordLength: Int): androidx.compose.ui.unit.Dp {
    return when {
        wordLength >= 9 -> 32.dp
        wordLength == 8 -> 36.dp
        wordLength == 7 -> 40.dp
        wordLength == 6 -> 44.dp
        else -> 48.dp
    }
}

private fun getResponsiveFontSize(wordLength: Int): androidx.compose.ui.unit.TextUnit {
    return when {
        wordLength >= 9 -> 16.sp
        wordLength == 8 -> 18.sp
        wordLength == 7 -> 20.sp
        else -> 22.sp
    }
}

@Composable
private fun GradientLetterTile(
    char: Char,
    sizeDp: androidx.compose.ui.unit.Dp,
    isAnswerSlot: Boolean,
    isSuccess: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "tile_press"
    )

    val backgroundBrush = remember(isSuccess) {
        if (isSuccess) {
            Brush.linearGradient(listOf(TealPrimary, EmeraldAccent))
        } else {
            Brush.linearGradient(listOf(IndigoAccent, PurpleAccent))
        }
    }

    Box(
        modifier = Modifier
            .size(sizeDp)
            .scale(scale)
            .shadow(
                elevation = if (isAnswerSlot) 6.dp else 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundBrush)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.uppercase(),
            color = Color.White,
            fontSize = getResponsiveFontSize(if (sizeDp < 36.dp) 9 else 6),
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GameStatsBar(
    score: Int,
    wordsSolved: Int,
    hintsUsed: Int,
    timeRemaining: Int
) {
    val timeProgress = (timeRemaining / 60f).coerceIn(0f, 1f)
    val timerColor = when {
        timeRemaining <= 10 -> RoseDanger
        timeRemaining <= 25 -> AmberAccent
        else -> CyanAccent
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardDark)
            .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        // Top stats chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                title = "SCORE",
                value = "$score",
                color = TealBright,
                icon = Icons.Default.EmojiEvents
            )
            StatItem(
                title = "SOLVED",
                value = "$wordsSolved",
                color = PurpleAccent,
                icon = Icons.Default.CheckCircle
            )
            StatItem(
                title = "HINTS",
                value = "$hintsUsed",
                color = AmberAccent,
                icon = Icons.Default.Lightbulb
            )
            StatItem(
                title = "TIME",
                value = "${timeRemaining}s",
                color = timerColor,
                icon = Icons.Default.Timer
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Bar
        Column {
            LinearProgressIndicator(
                progress = timeProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = timerColor,
                trackColor = BorderDark.copy(alpha = 0.6f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
        }
        Text(
            text = title,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun WordScrambleResults(
    score: Int,
    wordsSolved: Int,
    hintsUsed: Int,
    onPlayAgain: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val encouragement = remember(score) {
        when {
            score >= 8 -> "Zen Master! Your focus and clarity are razor-sharp. Mindful attention effortlessly defeated the scroll urge."
            score >= 5 -> "Mindful Achiever! Great presence of mind and calm thinking. You rewrote the impulse loop."
            score >= 2 -> "Centering Strong! Every mindful moment reclaims your mental space and presence."
            else -> "Gentle Start! A calm breath and steady practice will rewire your digital habits."
        }
    }

    val rankTitle = remember(score) {
        when {
            score >= 8 -> "\uD83C\uDFC6 Zen Master"
            score >= 5 -> "\uD83C\uDF1F Mindful Achiever"
            score >= 2 -> "\uD83C\uDF31 Focused Explorer"
            else -> "\u2728 Mindful Beginner"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Badge banner
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(IndigoAccent.copy(alpha = 0.2f), PurpleAccent.copy(alpha = 0.2f))
                    )
                )
                .border(1.dp, PurpleAccent.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = rankTitle,
                color = PurpleAccent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Game Complete!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Encouragement Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Text(
                text = encouragement,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // Stats Summary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(CardDark, SurfaceDark)
                    )
                )
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResultMetric(
                    label = "SCORE",
                    value = "$score",
                    color = TealBright
                )
                ResultMetric(
                    label = "WORDS",
                    value = "$wordsSolved",
                    color = PurpleAccent
                )
                ResultMetric(
                    label = "HINTS USED",
                    value = "$hintsUsed",
                    color = AmberAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Play Again Button
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = "Play Again",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Back to Menu Button
        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.linearGradient(listOf(BorderDark, BorderDark))
            )
        ) {
            Text(
                text = "Back to Menu",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResultMetric(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
