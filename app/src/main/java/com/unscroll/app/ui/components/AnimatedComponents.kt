package com.unscroll.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import com.unscroll.app.service.SoundSynthesizer

@Composable
fun AnimatedPressCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    playClickSound: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val context = LocalContext.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "press_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    HapticFeedback.triggerClick(context)
                    if (playClickSound) {
                        // Play a very subtle high-freq click
                        SoundSynthesizer.playSingingBowlChime(880f, 0.1f)
                    }
                    onClick()
                }
            ),
        contentAlignment = androidx.compose.ui.Alignment.Center,
        content = content
    )
}
