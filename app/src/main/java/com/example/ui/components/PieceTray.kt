package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Piece

@Composable
fun PieceTray(
    pieces: List<Piece>,
    selectedPieceId: String?,
    boardBounds: Rect?,
    onPieceClick: (pieceId: String) -> Unit,
    onDragStart: (piece: Piece) -> Unit,
    onDragMove: (screenPos: Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B2036),
                        Color(0xFF111424)
                    )
                )
            )
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .testTag("piece_tray"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 3) {
                val piece = pieces.getOrNull(i)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(105.dp)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141728))
                        .testTag("tray_slot_$i"),
                    contentAlignment = Alignment.Center
                ) {
                    if (piece != null && !piece.isPlaced) {
                        TrayPieceItem(
                            piece = piece,
                            isSelected = piece.id == selectedPieceId,
                            onClick = { onPieceClick(piece.id) },
                            onDragStart = { onDragStart(piece) },
                            onDragMove = onDragMove,
                            onDragEnd = onDragEnd
                        )
                    } else {
                        // Empty slot indicator
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.15f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "•",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrayPieceItem(
    piece: Piece,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragMove: (screenPos: Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    var slotRootPos by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                slotRootPos = coords.boundsInRoot().topLeft
            }
            .clickable(onClick = onClick)
            .pointerInput(piece.id) {
                detectDragGestures(
                    onDragStart = { localOffset ->
                        onDragStart()
                        onDragMove(slotRootPos + localOffset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onDragMove(slotRootPos + change.position)
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Compute adaptive block size based on shape dimensions
        val maxDim = maxOf(piece.shape.width, piece.shape.height)
        val blockSize = when {
            maxDim <= 2 -> 24.dp
            maxDim == 3 -> 20.dp
            maxDim == 4 -> 16.dp
            else -> 14.dp
        }

        PieceView(
            piece = piece,
            blockSize = blockSize,
            isSelected = isSelected,
            showDisabledLook = true
        )
    }
}
