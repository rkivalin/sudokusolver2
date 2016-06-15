package me.kivalin.sudokusolver.solvers

import me.kivalin.sudokusolver.sudoku.SudokuCoord
import me.kivalin.sudokusolver.sudoku.SudokuGrid

class HiddenSinglesSolver(val grid: SudokuGrid) {
    fun solve(): Map<SudokuCoord, Int> {
        return grid.houses.flatMap {
            val (candidates, multipleCandidates) = it.cells
                    .filter { it.value == 0 }
                    .map { Pair(it.candidates, 0) }
                    .fold(Pair(0, 0)) { a, b ->
                        Pair(a.first or b.first,
                                (a.first and b.first) or a.second or b.second)
                    }
            val selectValues = candidates and multipleCandidates.inv()
            it.cells.filter { it.value == 0 && it.candidates and selectValues != 0 }
                    .map {
                        val value = Integer.numberOfTrailingZeros(it.candidates and selectValues)
                        Pair(it.coord, value)
                    }
        }.associate { it }
    }
}
