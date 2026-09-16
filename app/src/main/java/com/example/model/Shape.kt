package com.example.model

import kotlin.random.Random

data class Shape(
    val id: String,
    val matrix: List<List<Int>>
) {
    val height: Int = matrix.size
    val width: Int = matrix.firstOrNull()?.size ?: 0
    val blockCount: Int = matrix.sumOf { row -> row.count { it == 1 } }

    /**
     * Relative cell coordinates where matrix[r][c] == 1.
     */
    val points: List<Pair<Int, Int>> = buildList {
        for (r in matrix.indices) {
            for (c in matrix[r].indices) {
                if (matrix[r][c] == 1) {
                    add(Pair(r, c))
                }
            }
        }
    }

    companion object {
        // 1x1
        val DOT = Shape("dot", listOf(listOf(1)))

        // 2-blocks
        val LINE_2_H = Shape("line_2_h", listOf(listOf(1, 1)))
        val LINE_2_V = Shape("line_2_v", listOf(listOf(1), listOf(1)))

        // 3-blocks
        val LINE_3_H = Shape("line_3_h", listOf(listOf(1, 1, 1)))
        val LINE_3_V = Shape("line_3_v", listOf(listOf(1), listOf(1), listOf(1)))
        val CORNER_2_TL = Shape("corner_2_tl", listOf(listOf(1, 1), listOf(1, 0)))
        val CORNER_2_TR = Shape("corner_2_tr", listOf(listOf(1, 1), listOf(0, 1)))
        val CORNER_2_BL = Shape("corner_2_bl", listOf(listOf(1, 0), listOf(1, 1)))
        val CORNER_2_BR = Shape("corner_2_br", listOf(listOf(0, 1), listOf(1, 1)))

        // 4-blocks
        val SQUARE_2X2 = Shape("sq_2x2", listOf(listOf(1, 1), listOf(1, 1)))
        val LINE_4_H = Shape("line_4_h", listOf(listOf(1, 1, 1, 1)))
        val LINE_4_V = Shape("line_4_v", listOf(listOf(1), listOf(1), listOf(1), listOf(1)))
        val T_UP = Shape("t_up", listOf(listOf(0, 1, 0), listOf(1, 1, 1)))
        val T_DOWN = Shape("t_down", listOf(listOf(1, 1, 1), listOf(0, 1, 0)))
        val T_LEFT = Shape("t_left", listOf(listOf(1, 0), listOf(1, 1), listOf(1, 0)))
        val T_RIGHT = Shape("t_right", listOf(listOf(0, 1), listOf(1, 1), listOf(0, 1)))
        val Z_H = Shape("z_h", listOf(listOf(1, 1, 0), listOf(0, 1, 1)))
        val S_H = Shape("s_h", listOf(listOf(0, 1, 1), listOf(1, 1, 0)))
        val Z_V = Shape("z_v", listOf(listOf(0, 1), listOf(1, 1), listOf(1, 0)))
        val S_V = Shape("s_v", listOf(listOf(1, 0), listOf(1, 1), listOf(0, 1)))
        val L_3X2_TL = Shape("l_3x2_tl", listOf(listOf(1, 1), listOf(1, 0), listOf(1, 0)))
        val L_3X2_TR = Shape("l_3x2_tr", listOf(listOf(1, 1), listOf(0, 1), listOf(0, 1)))
        val L_3X2_BL = Shape("l_3x2_bl", listOf(listOf(1, 0), listOf(1, 0), listOf(1, 1)))
        val L_3X2_BR = Shape("l_3x2_br", listOf(listOf(0, 1), listOf(0, 1), listOf(1, 1)))

        // 5-blocks
        val LINE_5_H = Shape("line_5_h", listOf(listOf(1, 1, 1, 1, 1)))
        val LINE_5_V = Shape("line_5_v", listOf(listOf(1), listOf(1), listOf(1), listOf(1), listOf(1)))
        val PLUS = Shape("plus", listOf(listOf(0, 1, 0), listOf(1, 1, 1), listOf(0, 1, 0)))
        val CORNER_3_BL = Shape("corner_3_bl", listOf(listOf(1, 0, 0), listOf(1, 0, 0), listOf(1, 1, 1)))
        val CORNER_3_BR = Shape("corner_3_br", listOf(listOf(0, 0, 1), listOf(0, 0, 1), listOf(1, 1, 1)))
        val CORNER_3_TL = Shape("corner_3_tl", listOf(listOf(1, 1, 1), listOf(1, 0, 0), listOf(1, 0, 0)))
        val CORNER_3_TR = Shape("corner_3_tr", listOf(listOf(1, 1, 1), listOf(0, 0, 1), listOf(0, 0, 1)))

        // Big shapes
        val SQUARE_3X3 = Shape("sq_3x3", listOf(listOf(1, 1, 1), listOf(1, 1, 1), listOf(1, 1, 1)))
        val RECT_2X3 = Shape("rect_2x3", listOf(listOf(1, 1, 1), listOf(1, 1, 1)))
        val RECT_3X2 = Shape("rect_3x2", listOf(listOf(1, 1), listOf(1, 1), listOf(1, 1)))

        val ALL_SHAPES = listOf(
            DOT,
            LINE_2_H, LINE_2_V,
            LINE_3_H, LINE_3_V,
            CORNER_2_TL, CORNER_2_TR, CORNER_2_BL, CORNER_2_BR,
            SQUARE_2X2,
            LINE_4_H, LINE_4_V,
            T_UP, T_DOWN, T_LEFT, T_RIGHT,
            Z_H, S_H, Z_V, S_V,
            L_3X2_TL, L_3X2_TR, L_3X2_BL, L_3X2_BR,
            LINE_5_H, LINE_5_V,
            PLUS,
            CORNER_3_BL, CORNER_3_BR, CORNER_3_TL, CORNER_3_TR,
            RECT_2X3, RECT_3X2,
            SQUARE_3X3
        )

        /**
         * Randomly selects a shape with balanced weights so massive shapes
         * (like 3x3) appear occasionally, while playable shapes appear frequently.
         */
        fun getRandomShape(): Shape {
            val roll = Random.nextFloat()
            return when {
                roll < 0.10f -> DOT
                roll < 0.30f -> listOf(LINE_2_H, LINE_2_V, LINE_3_H, LINE_3_V).random()
                roll < 0.50f -> listOf(SQUARE_2X2, CORNER_2_TL, CORNER_2_TR, CORNER_2_BL, CORNER_2_BR).random()
                roll < 0.70f -> listOf(LINE_4_H, LINE_4_V, T_UP, T_DOWN, T_LEFT, T_RIGHT, Z_H, S_H, Z_V, S_V).random()
                roll < 0.85f -> listOf(L_3X2_TL, L_3X2_TR, L_3X2_BL, L_3X2_BR, PLUS, LINE_5_H, LINE_5_V).random()
                roll < 0.95f -> listOf(CORNER_3_BL, CORNER_3_BR, CORNER_3_TL, CORNER_3_TR, RECT_2X3, RECT_3X2).random()
                else -> SQUARE_3X3
            }
        }
    }
}
