package com.example.model

enum class MascotMood {
    NORMAL,
    THINKING,
    CHEERING,
    EXCITED,
    SAD
}

data class GameState(
    val board: List<List<BlockColor?>> = List(8) { List(8) { null } },
    val pieces: List<Piece> = emptyList(),
    val selectedPieceId: String? = null,
    val draggedPiece: Piece? = null,
    val hoverRow: Int? = null,
    val hoverCol: Int? = null,
    val isHoverValid: Boolean = false,
    val potentialClearRows: Set<Int> = emptySet(),
    val potentialClearCols: Set<Int> = emptySet(),
    val clearingCells: Set<Pair<Int, Int>> = emptySet(),
    val score: Int = 0,
    val highScore: Int = 0,
    val comboStreak: Int = 0,
    val maxCombo: Int = 0,
    val totalLinesCleared: Int = 0,
    val comboBanner: String? = null,
    val pointsGained: Int? = null,
    val isGameOver: Boolean = false,
    val isNewHighScore: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val mascotMood: MascotMood = MascotMood.NORMAL,
    val mascotMessage: String = "Drag blocks onto the grid!"
) {
    val unplacedPieces: List<Piece>
        get() = pieces.filter { !it.isPlaced }
}
