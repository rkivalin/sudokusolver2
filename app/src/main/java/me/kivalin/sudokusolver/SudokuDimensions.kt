package me.kivalin.sudokusolver

import me.kivalin.sudokusolver.sudoku.SudokuConfig

data class SudokuDimensions(
        val viewWidthPx: Int,
        val viewHeightPx: Int,
        val config: SudokuConfig,
        val gridWidth: Int,
        val gridHeight: Int,
        val cellSizePx: Int
)
