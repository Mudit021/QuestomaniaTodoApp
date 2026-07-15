package com.example.questomaniato_doapp.ui.components

import androidx.compose.ui.graphics.Color
import com.example.questomaniato_doapp.model.Difficulty

fun difficultyColor(difficulty: Difficulty): Color = when (difficulty) {
    Difficulty.EASY -> Color(0xFF00DFA2)    // Emerald
    Difficulty.MEDIUM -> Color(0xFFFFB74D)  // Amber
    Difficulty.HARD -> Color(0xFFFF8A65)    // Orange
    Difficulty.EPIC -> Color(0xFFFF5252)    // Fire red
}

fun difficultyLabel(difficulty: Difficulty): String = when (difficulty) {
    Difficulty.EASY -> "Easy"
    Difficulty.MEDIUM -> "Medium"
    Difficulty.HARD -> "Hard"
    Difficulty.EPIC -> "Epic"
}
