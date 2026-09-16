package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.haptics.HapticManager
import com.example.model.BlockColor
import com.example.model.GameState
import com.example.model.MascotMood
import com.example.model.Piece
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("block_puzzle_prefs", Context.MODE_PRIVATE)
    private val soundManager = SoundManager()
    private val hapticManager = HapticManager(application)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        val savedHighScore = prefs.getInt(KEY_HIGH_SCORE, 0)
        _gameState.update { it.copy(highScore = savedHighScore) }
        startNewGame()
    }

    fun startNewGame() {
        val newBoard = List(8) { List<BlockColor?>(8) { null } }
        val newPieces = generateThreePieces(newBoard)
        val highScore = prefs.getInt(KEY_HIGH_SCORE, 0)

        _gameState.value = GameState(
            board = newBoard,
            pieces = newPieces,
            score = 0,
            highScore = highScore,
            comboStreak = 0,
            maxCombo = 0,
            totalLinesCleared = 0,
            isGameOver = false,
            isNewHighScore = false,
            isSoundEnabled = soundManager.isEnabled,
            mascotMood = MascotMood.NORMAL,
            mascotMessage = "Place blocks to clear lines!"
        )
    }

    private fun generateThreePieces(currentBoard: List<List<BlockColor?>>): List<Piece> {
        val pieces = listOf(
            Piece.createRandom(),
            Piece.createRandom(),
            Piece.createRandom()
        )
        // Check fit for each piece on the board
        return pieces.map { piece ->
            piece.copy(canFit = canPieceFitAnywhere(piece, currentBoard))
        }
    }

    fun toggleSound() {
        val newState = !soundManager.isEnabled
        soundManager.isEnabled = newState
        _gameState.update { it.copy(isSoundEnabled = newState) }
    }

    fun onPieceSelected(pieceId: String) {
        val currentSelected = _gameState.value.selectedPieceId
        if (currentSelected == pieceId) {
            // Deselect
            _gameState.update { it.copy(selectedPieceId = null) }
        } else {
            hapticManager.vibrateTap()
            soundManager.playPickup()
            _gameState.update {
                it.copy(
                    selectedPieceId = pieceId,
                    mascotMood = MascotMood.THINKING,
                    mascotMessage = "Tap on the board to place it!"
                )
            }
        }
    }

    fun onDragStart(piece: Piece) {
        hapticManager.vibrateTap()
        soundManager.playPickup()
        _gameState.update {
            it.copy(
                draggedPiece = piece,
                selectedPieceId = piece.id,
                mascotMood = MascotMood.THINKING,
                mascotMessage = "Aim for full rows or columns!"
            )
        }
    }

    fun onDragHover(row: Int?, col: Int?) {
        val dragged = _gameState.value.draggedPiece ?: return
        val currentBoard = _gameState.value.board

        if (row == null || col == null) {
            _gameState.update {
                it.copy(
                    hoverRow = null,
                    hoverCol = null,
                    isHoverValid = false,
                    potentialClearRows = emptySet(),
                    potentialClearCols = emptySet()
                )
            }
            return
        }

        val isValid = isValidPlacement(dragged, row, col, currentBoard)
        if (isValid) {
            val (fullRows, fullCols) = calculateHypotheticalClears(dragged, row, col, currentBoard)
            _gameState.update {
                it.copy(
                    hoverRow = row,
                    hoverCol = col,
                    isHoverValid = true,
                    potentialClearRows = fullRows,
                    potentialClearCols = fullCols
                )
            }
        } else {
            _gameState.update {
                it.copy(
                    hoverRow = row,
                    hoverCol = col,
                    isHoverValid = false,
                    potentialClearRows = emptySet(),
                    potentialClearCols = emptySet()
                )
            }
        }
    }

    fun onDragEnd(placed: Boolean) {
        val state = _gameState.value
        val dragged = state.draggedPiece

        if (!placed || dragged == null || state.hoverRow == null || state.hoverCol == null || !state.isHoverValid) {
            // Cancelled or invalid placement
            _gameState.update {
                it.copy(
                    draggedPiece = null,
                    hoverRow = null,
                    hoverCol = null,
                    isHoverValid = false,
                    potentialClearRows = emptySet(),
                    potentialClearCols = emptySet(),
                    mascotMood = if (it.comboStreak > 0) MascotMood.CHEERING else MascotMood.NORMAL,
                    mascotMessage = if (it.comboStreak > 0) "Keep the streak going!" else "Find the best fit!"
                )
            }
        } else {
            // Place the piece!
            placePiece(dragged, state.hoverRow, state.hoverCol)
        }
    }

    fun onCellClicked(row: Int, col: Int) {
        val selectedId = _gameState.value.selectedPieceId ?: return
        val piece = _gameState.value.unplacedPieces.find { it.id == selectedId } ?: return

        if (isValidPlacement(piece, row, col, _gameState.value.board)) {
            placePiece(piece, row, col)
        } else {
            hapticManager.vibrateTap()
            soundManager.playInvalid()
            _gameState.update {
                it.copy(
                    mascotMood = MascotMood.THINKING,
                    mascotMessage = "Can't place it there, try another spot!"
                )
            }
        }
    }

    private fun placePiece(piece: Piece, originRow: Int, originCol: Int) {
        val state = _gameState.value
        val board = state.board.map { it.toMutableList() }

        // 1. Fill board cells
        for ((dr, dc) in piece.shape.points) {
            board[originRow + dr][originCol + dc] = piece.color
        }

        // 2. Mark piece as placed
        val updatedPieces = state.pieces.map {
            if (it.id == piece.id) it.copy(isPlaced = true) else it
        }

        // 3. Detect completed rows and columns
        val fullRows = (0 until 8).filter { r -> (0 until 8).all { c -> board[r][c] != null } }.toSet()
        val fullCols = (0 until 8).filter { c -> (0 until 8).all { r -> board[r][c] != null } }.toSet()
        val totalLines = fullRows.size + fullCols.size

        // Placement block points
        val placementPoints = piece.shape.blockCount

        if (totalLines > 0) {
            // Line clear!
            val newStreak = state.comboStreak + 1
            val maxCombo = maxOf(state.maxCombo, newStreak)
            val linePoints = 10 * totalLines * (totalLines + 1) / 2
            val streakBonus = (newStreak - 1) * 20 * totalLines
            val addedPoints = placementPoints + linePoints + streakBonus
            val newScore = state.score + addedPoints
            val totalLinesCleared = state.totalLinesCleared + totalLines

            // Determine combo banner and mascot mood
            val bannerText = when {
                totalLines >= 3 -> "TRIPLE BLAST! +$addedPoints"
                totalLines == 2 && newStreak > 1 -> "COMBO x$newStreak! +$addedPoints"
                totalLines == 2 -> "DOUBLE CLEAR! +$addedPoints"
                newStreak >= 3 -> "STREAK x$newStreak! +$addedPoints"
                newStreak == 2 -> "COMBO x2! +$addedPoints"
                else -> "LINE CLEAR! +$addedPoints"
            }

            val mascotMood = if (newStreak >= 2 || totalLines >= 2) MascotMood.EXCITED else MascotMood.CHEERING
            val mascotMsg = when {
                newStreak >= 3 -> "UNSTOPPABLE! 🔥"
                totalLines >= 2 -> "SPECTACULAR CLEAR!"
                newStreak == 2 -> "Great combo! Keep going!"
                else -> "Awesome blast!"
            }

            // Identify clearing cells for animation
            val clearingCells = buildSet {
                for (r in fullRows) {
                    for (c in 0 until 8) add(Pair(r, c))
                }
                for (c in fullCols) {
                    for (r in 0 until 8) add(Pair(r, c))
                }
            }

            hapticManager.vibrateBlast()
            soundManager.playLineClear(newStreak)
            if (newStreak >= 3 || totalLines >= 3) {
                soundManager.playComboFanfare()
            }

            // High score check
            val (highScore, isNewHigh) = checkHighScore(newScore, state.highScore)

            _gameState.update {
                it.copy(
                    board = board.map { rowList -> rowList.toList() },
                    pieces = updatedPieces,
                    selectedPieceId = null,
                    draggedPiece = null,
                    hoverRow = null,
                    hoverCol = null,
                    isHoverValid = false,
                    potentialClearRows = emptySet(),
                    potentialClearCols = emptySet(),
                    clearingCells = clearingCells,
                    score = newScore,
                    highScore = highScore,
                    isNewHighScore = isNewHigh,
                    comboStreak = newStreak,
                    maxCombo = maxCombo,
                    totalLinesCleared = totalLinesCleared,
                    comboBanner = bannerText,
                    pointsGained = addedPoints,
                    mascotMood = mascotMood,
                    mascotMessage = mascotMsg
                )
            }

            // Launch coroutine to clear the cells after animation
            viewModelScope.launch {
                delay(280) // pop animation duration
                // Clear cells in the board
                for (r in fullRows) {
                    for (c in 0 until 8) {
                        board[r][c] = null
                    }
                }
                for (c in fullCols) {
                    for (r in 0 until 8) {
                        board[r][c] = null
                    }
                }

                val finalizedBoard = board.map { rowList -> rowList.toList() }
                finalizeTurn(finalizedBoard, updatedPieces)
            }
        } else {
            // No line clear
            val addedPoints = placementPoints
            val newScore = state.score + addedPoints
            val (highScore, isNewHigh) = checkHighScore(newScore, state.highScore)

            hapticManager.vibrateSnap()
            soundManager.playDrop()

            val finalizedBoard = board.map { rowList -> rowList.toList() }

            _gameState.update {
                it.copy(
                    board = finalizedBoard,
                    pieces = updatedPieces,
                    selectedPieceId = null,
                    draggedPiece = null,
                    hoverRow = null,
                    hoverCol = null,
                    isHoverValid = false,
                    potentialClearRows = emptySet(),
                    potentialClearCols = emptySet(),
                    clearingCells = emptySet(),
                    score = newScore,
                    highScore = highScore,
                    isNewHighScore = isNewHigh,
                    comboStreak = 0,
                    comboBanner = null,
                    pointsGained = addedPoints,
                    mascotMood = MascotMood.NORMAL,
                    mascotMessage = "Solid move!"
                )
            }

            finalizeTurn(finalizedBoard, updatedPieces)
        }
    }

    private fun finalizeTurn(currentBoard: List<List<BlockColor?>>, currentPieces: List<Piece>) {
        val remainingPieces = currentPieces.filter { !it.isPlaced }
        val nextPieces = if (remainingPieces.isEmpty()) {
            // Hand is exhausted -> spawn 3 fresh pieces!
            generateThreePieces(currentBoard)
        } else {
            // Recompute canFit for remaining pieces
            currentPieces.map { piece ->
                if (!piece.isPlaced) {
                    piece.copy(canFit = canPieceFitAnywhere(piece, currentBoard))
                } else {
                    piece
                }
            }
        }

        // Check if ANY unplaced piece can fit
        val unplaced = nextPieces.filter { !it.isPlaced }
        val canAnyPieceFit = unplaced.any { it.canFit }

        if (!canAnyPieceFit && unplaced.isNotEmpty()) {
            // Game Over!
            soundManager.playGameOver()
            _gameState.update {
                it.copy(
                    board = currentBoard,
                    pieces = nextPieces,
                    clearingCells = emptySet(),
                    comboBanner = null,
                    isGameOver = true,
                    mascotMood = MascotMood.SAD,
                    mascotMessage = "Game Over! Great try!"
                )
            }
        } else {
            _gameState.update {
                it.copy(
                    board = currentBoard,
                    pieces = nextPieces,
                    clearingCells = emptySet()
                )
            }
        }
    }

    private fun checkHighScore(currentScore: Int, existingHighScore: Int): Pair<Int, Boolean> {
        return if (currentScore > existingHighScore) {
            prefs.edit().putInt(KEY_HIGH_SCORE, currentScore).apply()
            Pair(currentScore, true)
        } else {
            Pair(existingHighScore, false)
        }
    }

    private fun isValidPlacement(
        piece: Piece,
        originRow: Int,
        originCol: Int,
        board: List<List<BlockColor?>>
    ): Boolean {
        for ((dr, dc) in piece.shape.points) {
            val r = originRow + dr
            val c = originCol + dc
            if (r !in 0 until 8 || c !in 0 until 8) return false
            if (board[r][c] != null) return false
        }
        return true
    }

    private fun canPieceFitAnywhere(piece: Piece, board: List<List<BlockColor?>>): Boolean {
        for (r in 0 until 8) {
            for (c in 0 until 8) {
                if (isValidPlacement(piece, r, c, board)) {
                    return true
                }
            }
        }
        return false
    }

    private fun calculateHypotheticalClears(
        piece: Piece,
        originRow: Int,
        originCol: Int,
        board: List<List<BlockColor?>>
    ): Pair<Set<Int>, Set<Int>> {
        val tempBoard = Array(8) { r -> Array(8) { c -> board[r][c] != null } }
        for ((dr, dc) in piece.shape.points) {
            val r = originRow + dr
            val c = originCol + dc
            if (r in 0 until 8 && c in 0 until 8) {
                tempBoard[r][c] = true
            }
        }
        val fullRows = (0 until 8).filter { r -> (0 until 8).all { c -> tempBoard[r][c] } }.toSet()
        val fullCols = (0 until 8).filter { c -> (0 until 8).all { r -> tempBoard[r][c] } }.toSet()
        return Pair(fullRows, fullCols)
    }

    companion object {
        private const val KEY_HIGH_SCORE = "high_score"
    }
}
