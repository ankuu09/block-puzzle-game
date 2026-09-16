package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BlockColor
import com.example.model.Piece

@Composable
fun BoardView(
    board: List<List<BlockColor?>>,
    hoverRow: Int?,
    hoverCol: Int?,
    isHoverValid: Boolean,
    activePiece: Piece?,
    potentialClearRows: Set<Int>,
    potentialClearCols: Set<Int>,
    clearingCells: Set<Pair<Int, Int>>,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
    onBoardPositioned: (Rect) -> Unit = {}
) {
    // Determine ghost cells
    val ghostCells = remember(hoverRow, hoverCol, isHoverValid, activePiece) {
        if (hoverRow != null && hoverCol != null && isHoverValid && activePiece != null) {
            activePiece.shape.points.map { (dr, dc) -> Pair(hoverRow + dr, hoverCol + dc) }.toSet()
        } else {
            emptySet()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp)
            .aspectRatio(1f)
            .padding(12.dp)
            .onGloballyPositioned { coordinates ->
                onBoardPositioned(coordinates.boundsInRoot())
            }
            .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF38BDF8).copy(alpha = 0.25f))
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2338),
                        Color(0xFF121526)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3B82F6).copy(alpha = 0.5f),
                        Color(0xFF8B5CF6).copy(alpha = 0.3f),
                        Color(0xFF1E293B)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(8.dp)
            .testTag("game_board"),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val totalSpacing = 2.dp * 9
            val cellSize = (maxWidth - totalSpacing) / 8

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (r in 0 until 8) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (c in 0 until 8) {
                            val color = board.getOrNull(r)?.getOrNull(c)
                            val isGhost = (r to c) in ghostCells
                            val isClearing = (r to c) in clearingCells
                            val isPotentialClear = r in potentialClearRows || c in potentialClearCols

                            BlockCell(
                                color = color,
                                isGhost = isGhost,
                                ghostColor = activePiece?.color,
                                isClearing = isClearing,
                                isPotentialClear = isPotentialClear,
                                cellSize = cellSize,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp),
                                onClick = { onCellClick(r, c) }
                            )
                        }
                    }
                }
            }
        }
    }
}
