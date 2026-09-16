package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.Piece

@Composable
fun PieceView(
    piece: Piece,
    modifier: Modifier = Modifier,
    blockSize: Dp = 24.dp,
    isSelected: Boolean = false,
    showDisabledLook: Boolean = true
) {
    val alpha = if (!piece.canFit && showDisabledLook) 0.38f else 1f
    val matrix = piece.shape.matrix
    val shapeBg = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .testTag("piece_view_${piece.id}")
            .alpha(alpha)
            .then(
                if (isSelected) {
                    Modifier
                        .background(
                            color = piece.color.primary.copy(alpha = 0.15f),
                            shape = shapeBg
                        )
                        .border(
                            width = 2.dp,
                            color = piece.color.light,
                            shape = shapeBg
                        )
                        .padding(4.dp)
                } else {
                    Modifier.padding(4.dp)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column {
            for (r in matrix.indices) {
                Row {
                    for (c in matrix[r].indices) {
                        if (matrix[r][c] == 1) {
                            BlockCell(
                                color = piece.color,
                                cellSize = blockSize,
                                modifier = Modifier
                                    .size(blockSize)
                                    .padding(1.dp)
                            )
                        } else {
                            // Empty invisible slot inside bounding box
                            Box(
                                modifier = Modifier
                                    .size(blockSize)
                                    .padding(1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
