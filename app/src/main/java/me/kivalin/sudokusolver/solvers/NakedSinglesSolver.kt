package me.kivalin.sudokusolver.solvers

import me.kivalin.sudokusolver.sudoku.SudokuCoord
import me.kivalin.sudokusolver.sudoku.SudokuGrid

class NakedSinglesSolver(val grid: SudokuGrid) {
    fun solve(): Map<SudokuCoord, Int> {
        return grid.cells.filter { it.value == 0 && Integer.bitCount(it.candidates) == 1 }
                .associate { Pair(it.coord, Integer.numberOfTrailingZeros(it.candidates)) }
    }
}
