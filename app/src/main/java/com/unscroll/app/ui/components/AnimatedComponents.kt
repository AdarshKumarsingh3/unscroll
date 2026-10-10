package com.unscroll.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.unscroll.app.service.SoundSynthesizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    HapticFeedback.triggerClick(context)
                    if (playClickSound) {
                        SoundSynthesizer.playTick()
                    }
                    onClick()
                }
            ),
        contentAlignment = androidx.compose.ui.Alignment.Center,
        content = content
    )
}

/** Card that fades + slides in on first composition */
@Composable
fun AnimatedEntryCard(
    modifier: Modifier = Modifier,
    index: Int = 0,
    content: @Composable BoxScope.() -> Unit
) {
    val enterAlpha = remember { Animatable(0f) }
    val enterOffset = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        delay(index * 80L)
        launch {
            enterAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        }
        enterOffset.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = enterAlpha.value
                translationY = enterOffset.value
            },
        content = content
    )
}
