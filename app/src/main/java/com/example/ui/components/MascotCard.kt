package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MascotMood

@Composable
fun MascotCard(
    mood: MascotMood,
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1E243D),
                        Color(0xFF181C30)
                    )
                )
            )
            .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("mascot_companion_card"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mascot Avatar image with mood ring
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = when (mood) {
                        MascotMood.EXCITED, MascotMood.CHEERING -> Color(0xFFFFD54F)
                        MascotMood.THINKING -> Color(0xFF38BDF8)
                        MascotMood.SAD -> Color(0xFFF43F5E)
                        MascotMood.NORMAL -> Color(0xFF818CF8)
                    },
                    shape = CircleShape
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascot_companion),
                contentDescription = "Mascot Companion",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Speech bubble / status message
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "BLAST BOT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                val moodEmoji = when (mood) {
                    MascotMood.EXCITED -> "⚡"
                    MascotMood.CHEERING -> "🎉"
                    MascotMood.THINKING -> "💭"
                    MascotMood.SAD -> "💔"
                    MascotMood.NORMAL -> "🤖"
                }
                Text(text = moodEmoji, fontSize = 11.sp)
            }

            AnimatedContent(
                targetState = message,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "mascot_speech"
            ) { targetText ->
                Text(
                    text = targetText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE2E8F0),
                    maxLines = 1
                )
            }
        }
    }
}
