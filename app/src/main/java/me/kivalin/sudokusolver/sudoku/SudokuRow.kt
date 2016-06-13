package me.kivalin.sudokusolver.sudoku

class SudokuRow(grid: SudokuGrid, coord: SudokuCoord) : SudokuHouse(grid, coord) {
    override fun contains(cell: SudokuCell): Boolean {
        return cell.coord.y == coord.y
                && coord.x <= cell.coord.x && cell.coord.x < coord.x + grid.config.size
    }
}
