package com.example.model

import java.util.UUID

data class Piece(
    val id: String = UUID.randomUUID().toString(),
    val shape: Shape,
    val color: BlockColor,
    val isPlaced: Boolean = false,
    val canFit: Boolean = true
) {
    companion object {
        fun createRandom(): Piece {
            val shape = Shape.getRandomShape()
            val color = BlockColor.random()
            return Piece(shape = shape, color = color)
        }
    }
}
