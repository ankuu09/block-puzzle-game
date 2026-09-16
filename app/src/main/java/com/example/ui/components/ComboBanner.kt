package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComboBanner(
    bannerText: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = bannerText != null,
        enter = fadeIn(tween(150)) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialScale = 0.5f
        ),
        exit = fadeOut(tween(250)) + scaleOut(targetScale = 1.1f),
        modifier = modifier
    ) {
        if (bannerText != null) {
            Box(
                modifier = Modifier
                    .rotate(-2.5f)
                    .shadow(16.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFFFD54F))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF3366),
                                Color(0xFFFF9900),
                                Color(0xFFFFCC00)
                            )
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .testTag("combo_banner"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bannerText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
        }
    }
}
