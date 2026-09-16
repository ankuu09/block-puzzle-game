package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun HowToPlayDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E233D),
                            Color(0xFF121527)
                        )
                    )
                )
                .border(1.5.dp, Color(0xFF3B82F6).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("how_to_play_dialog")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "HOW TO PLAY",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                RuleItem(
                    emoji = "🧩",
                    title = "Place Blocks",
                    description = "Drag blocks from the bottom tray onto the 8×8 board, or tap a block then tap a grid spot."
                )

                Spacer(modifier = Modifier.height(12.dp))

                RuleItem(
                    emoji = "💥",
                    title = "Clear Rows & Columns",
                    description = "Fill an entire horizontal row or vertical column to blast it clear and free up grid space."
                )

                Spacer(modifier = Modifier.height(12.dp))

                RuleItem(
                    emoji = "🔥",
                    title = "Combos & High Scores",
                    description = "Clear multiple lines at once or clear lines on consecutive moves to earn huge multiplier bonuses!"
                )

                Spacer(modifier = Modifier.height(12.dp))

                RuleItem(
                    emoji = "🎯",
                    title = "No Falling Blocks / No Timer",
                    description = "Take your time! The game ends only when none of your available pieces can fit on the board."
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_how_to_play"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text(
                        text = "GOT IT!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleItem(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141829))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFF1F2644))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 16.sp
            )
        }
    }
}
