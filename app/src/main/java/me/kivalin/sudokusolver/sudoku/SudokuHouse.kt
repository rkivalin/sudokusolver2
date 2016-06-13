package me.kivalin.sudokusolver.sudoku

abstract class SudokuHouse(val grid: SudokuGrid, val coord: SudokuCoord) {
    abstract fun contains(cell: SudokuCell): Boolean

    val cells: List<SudokuCell>
        get() = grid.cells.filter { contains(it) }
}