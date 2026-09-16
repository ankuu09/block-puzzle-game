package com.example.model

import androidx.compose.ui.graphics.Color

enum class BlockColor(
    val primary: Color,
    val light: Color,
    val dark: Color,
    val glow: Color
) {
    AMBER(
        primary = Color(0xFFFF9800),
        light = Color(0xFFFFC947),
        dark = Color(0xFFC66900),
        glow = Color(0x66FFA726)
    ),
    CYAN(
        primary = Color(0xFF00BCD4),
        light = Color(0xFF62EFFF),
        dark = Color(0xFF008BA3),
        glow = Color(0x6626C6DA)
    ),
    EMERALD(
        primary = Color(0xFF4CAF50),
        light = Color(0xFF80E27E),
        dark = Color(0xFF087F23),
        glow = Color(0x6666BB6A)
    ),
    PURPLE(
        primary = Color(0xFF9C27B0),
        light = Color(0xFFD05CE3),
        dark = Color(0xFF6A0080),
        glow = Color(0x66AB47BC)
    ),
    RUBY(
        primary = Color(0xFFE91E63),
        light = Color(0xFFFF6090),
        dark = Color(0xFFB0003A),
        glow = Color(0x66EC407A)
    ),
    BLUE(
        primary = Color(0xFF2196F3),
        light = Color(0xFF6EC6FF),
        dark = Color(0xFF0069C0),
        glow = Color(0x6642A5F5)
    ),
    GOLD(
        primary = Color(0xFFFFC107),
        light = Color(0xFFFFF350),
        dark = Color(0xFFC79100),
        glow = Color(0x66FFCA28)
    ),
    CORAL(
        primary = Color(0xFFFF5722),
        light = Color(0xFFFF8A50),
        dark = Color(0xFFC41C00),
        glow = Color(0x66FF7043)
    );

    companion object {
        fun random(excluding: BlockColor? = null): BlockColor {
            val values = entries.filter { it != excluding }
            return values.random()
        }
    }
}
