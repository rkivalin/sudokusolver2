package me.kivalin.sudokusolver

import me.kivalin.sudokusolver.sudoku.SudokuCell

interface SudokuListener {
    fun onSelectValue(cell: SudokuCell, value: Int)
}
