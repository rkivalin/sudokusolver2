package me.kivalin.sudokusolver.sudoku

class SudokuCell(
        val grid: SudokuGrid,
        val coord: SudokuCoord,
        val value: Int) {

    val candidates: Int
        get() = houses.flatMap { it.cells }
                .filter { it.value != 0 && it !== this }
                .map { (1 shl it.value).inv() }
                .fold(1.shl(grid.config.size).minus(1).shl(1), Int::and)

    val houses: List<SudokuHouse>
        get() = grid.houses.filter { it.contains(this) }

    val rows: List<SudokuRow>
        get() = grid.rows.filter { it.contains(this) }

    val columns: List<SudokuColumn>
        get() = grid.columns.filter { it.contains(this) }

    val boxes: List<SudokuBox>
        get() = grid.boxes.filter { it.contains(this) }
}