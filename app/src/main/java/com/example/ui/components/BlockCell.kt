package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.BlockColor

@Composable
fun BlockCell(
    color: BlockColor?,
    modifier: Modifier = Modifier,
    isGhost: Boolean = false,
    ghostColor: BlockColor? = null,
    isClearing: Boolean = false,
    isPotentialClear: Boolean = false,
    cellSize: Dp = 38.dp,
    onClick: (() -> Unit)? = null
) {
    val cornerRadius = (cellSize * 0.18f).coerceAtLeast(3.dp)
    val shape = RoundedCornerShape(cornerRadius)

    // Clear animation scale & flash
    val clearScale = remember { Animatable(1f) }
    val clearAlpha = remember { Animatable(1f) }

    LaunchedEffect(isClearing) {
        if (isClearing) {
            clearScale.animateTo(
                targetValue = 1.35f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
            clearAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 80)
            )
        } else {
            clearScale.snapTo(1f)
            clearAlpha.snapTo(1f)
        }
    }

    // Potential clear pulse animation
    val pulseAlpha = remember { Animatable(0.6f) }
    LaunchedEffect(isPotentialClear) {
        if (isPotentialClear) {
            pulseAlpha.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseAlpha.snapTo(0.6f)
        }
    }

    val baseModifier = modifier
        .scale(clearScale.value)
        .testTag("block_cell")
        .then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )

    if (color != null) {
        // Filled 3D block
        Box(
            modifier = baseModifier
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.light,
                            color.primary,
                            color.dark
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    color = if (isPotentialClear) Color.White.copy(alpha = pulseAlpha.value) else color.light.copy(alpha = 0.7f),
                    shape = shape
                )
        ) {
            // Top-left bevel specular shine highlight
            Box(
                modifier = Modifier
                    .fillMaxSize(0.85f)
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(topStart = cornerRadius, bottomEnd = cornerRadius * 0.5f))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isPotentialClear) 0.55f else 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Center jewel facet / inner embossed plate
            Box(
                modifier = Modifier
                    .fillMaxSize(0.65f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(cornerRadius * 0.6f))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                color.light.copy(alpha = 0.4f),
                                color.dark.copy(alpha = 0.4f)
                            )
                        )
                    )
                    .border(
                        width = 0.8.dp,
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(cornerRadius * 0.6f)
                    )
            )
        }
    } else if (isGhost && ghostColor != null) {
        // Ghost hover preview
        Box(
            modifier = baseModifier
                .clip(shape)
                .background(ghostColor.primary.copy(alpha = 0.45f))
                .border(
                    width = 1.5.dp,
                    color = ghostColor.light.copy(alpha = 0.9f),
                    shape = shape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.7f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(cornerRadius * 0.6f))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    } else {
        // Empty board cell slot
        Box(
            modifier = baseModifier
                .clip(shape)
                .background(
                    if (isPotentialClear) {
                        Color(0xFF2E3456).copy(alpha = pulseAlpha.value)
                    } else {
                        Color(0xFF141729)
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (isPotentialClear) {
                        Color(0xFFFFD54F).copy(alpha = pulseAlpha.value)
                    } else {
                        Color(0xFF222744)
                    },
                    shape = shape
                )
        ) {
            // Subtle inset shadow effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        shape = shape
                    )
            )
        }
    }
}
