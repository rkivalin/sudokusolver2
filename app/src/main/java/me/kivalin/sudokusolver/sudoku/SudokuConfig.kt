package me.kivalin.sudokusolver.sudoku

import java.util.*

class SudokuConfig(
        val boxWidth: Int,
        val boxHeight: Int,
        val grids: List<SudokuCoord> = listOf(SudokuCoord(0, 0))) {

    val size: Int
        get() = boxWidth * boxHeight

    val gridWidth: Int
        get() = grids.map { it.x }.max()?.plus(size) ?: throw IllegalStateException()

    val gridHeight: Int
        get() = grids.map { it.y }.max()?.plus(size) ?: throw IllegalStateException()

    init {
        checkBoxSize(boxWidth, boxHeight)
        checkGrids(grids)
    }

    private fun checkBoxSize(boxWidth: Int, boxHeight: Int) {
        if (boxWidth <= 0 || boxHeight <= 0) {
            throw IllegalArgumentException(String.format(Locale.ROOT,
                    "boxWidth=%d boxHeight=%d", boxWidth, boxHeight));
        }
    }

    private fun checkGrids(grids: List<SudokuCoord>) {
        if (grids.isEmpty()) {
            throw IllegalArgumentException("empty list of grids")
        }
        grids.forEachIndexed { i, grid ->
            if (grid.x < 0 || grid.y < 0) {
                throw IllegalArgumentException(String.format(Locale.ROOT,
                        "grid.x=%d grid.y=%d at index=%d", grid.x, grid.y, i))
            }
        }
    }
}
