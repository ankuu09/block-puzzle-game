package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Piece
import com.example.ui.components.BoardView
import com.example.ui.components.ComboBanner
import com.example.ui.components.GameOverDialog
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.MascotCard
import com.example.ui.components.PieceTray
import com.example.ui.components.PieceView
import com.example.ui.components.TopBar
import com.example.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun BlockPuzzleScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.gameState.collectAsState()
    var boardBounds by remember { mutableStateOf<Rect?>(null) }
    var showHowToPlay by remember { mutableStateOf(false) }

    // Drag state
    var isDragging by remember { mutableStateOf(false) }
    var activeDragPiece by remember { mutableStateOf<Piece?>(null) }
    var dragTouchPos by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current
    // Lift dragged piece by 75dp so thumb doesn't occlude board cells
    val verticalLiftPx = with(density) { 75.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1221),
                        Color(0xFF090A14)
                    )
                )
            )
            .systemBarsPadding()
            .testTag("block_puzzle_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar (Scores, controls)
            TopBar(
                score = state.score,
                highScore = state.highScore,
                comboStreak = state.comboStreak,
                isSoundEnabled = state.isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onRestart = { viewModel.startNewGame() },
                onShowHowToPlay = { showHowToPlay = true }
            )

            // 2. 8x8 Board Container with overlaid Combo Banner
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val activePiece = if (isDragging) {
                    activeDragPiece
                } else {
                    state.unplacedPieces.find { it.id == state.selectedPieceId }
                }

                BoardView(
                    board = state.board,
                    hoverRow = state.hoverRow,
                    hoverCol = state.hoverCol,
                    isHoverValid = state.isHoverValid,
                    activePiece = activePiece,
                    potentialClearRows = state.potentialClearRows,
                    potentialClearCols = state.potentialClearCols,
                    clearingCells = state.clearingCells,
                    onCellClick = { r, c -> viewModel.onCellClicked(r, c) },
                    onBoardPositioned = { rect -> boardBounds = rect }
                )

                // Combo banner popup
                ComboBanner(
                    bannerText = state.comboBanner,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 3. Cartoon Mascot Companion
            MascotCard(
                mood = state.mascotMood,
                message = state.mascotMessage
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 4. Piece Tray with 3 available pieces
            PieceTray(
                pieces = state.pieces,
                selectedPieceId = state.selectedPieceId,
                boardBounds = boardBounds,
                onPieceClick = { pieceId -> viewModel.onPieceSelected(pieceId) },
                onDragStart = { piece ->
                    activeDragPiece = piece
                    isDragging = true
                    viewModel.onDragStart(piece)
                },
                onDragMove = { screenPos ->
                    dragTouchPos = screenPos
                    val bounds = boardBounds
                    if (bounds != null && bounds.width > 0) {
                        val cellW = bounds.width / 8f
                        val cellH = bounds.height / 8f
                        val floatX = screenPos.x
                        val floatY = screenPos.y - verticalLiftPx

                        // Check if within bounds
                        if (floatX >= bounds.left - cellW * 0.5f &&
                            floatX <= bounds.right + cellW * 0.5f &&
                            floatY >= bounds.top - cellH * 0.5f &&
                            floatY <= bounds.bottom + cellH * 0.5f
                        ) {
                            val candidateCol = ((floatX - bounds.left) / cellW).toInt().coerceIn(0, 7)
                            val candidateRow = ((floatY - bounds.top) / cellH).toInt().coerceIn(0, 7)
                            viewModel.onDragHover(candidateRow, candidateCol)
                        } else {
                            viewModel.onDragHover(null, null)
                        }
                    }
                },
                onDragEnd = {
                    isDragging = false
                    viewModel.onDragEnd(placed = true)
                    activeDragPiece = null
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Floating Drag Overlay (renders the lifted piece following the touch position)
        if (isDragging && activeDragPiece != null) {
            val piece = activeDragPiece!!
            val bounds = boardBounds
            val boardCellSizeDp = if (bounds != null && bounds.width > 0) {
                with(density) { (bounds.width / 8f).toDp() }
            } else {
                38.dp
            }

            val piecePxW = with(density) { (piece.shape.width * boardCellSizeDp.value).dp.toPx() }
            val piecePxH = with(density) { (piece.shape.height * boardCellSizeDp.value).dp.toPx() }

            val floatX = dragTouchPos.x - piecePxW / 2f
            val floatY = dragTouchPos.y - verticalLiftPx - piecePxH / 2f

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(floatX.roundToInt(), floatY.roundToInt())
                    }
            ) {
                PieceView(
                    piece = piece,
                    blockSize = boardCellSizeDp,
                    isSelected = false,
                    showDisabledLook = false
                )
            }
        }

        // Game Over Dialog
        if (state.isGameOver) {
            GameOverDialog(
                score = state.score,
                highScore = state.highScore,
                isNewHighScore = state.isNewHighScore,
                totalLinesCleared = state.totalLinesCleared,
                maxCombo = state.maxCombo,
                onPlayAgain = { viewModel.startNewGame() }
            )
        }

        // How To Play Dialog
        if (showHowToPlay) {
            HowToPlayDialog(onDismiss = { showHowToPlay = false })
        }
    }
}
