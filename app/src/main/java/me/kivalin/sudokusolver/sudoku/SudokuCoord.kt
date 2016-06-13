package me.kivalin.sudokusolver.sudoku

data class SudokuCoord(val x: Int, val y: Int) {
    fun offsetBy(x: Int, y: Int): SudokuCoord {
        return copy(this.x + x, this.y + y)
    }
}
