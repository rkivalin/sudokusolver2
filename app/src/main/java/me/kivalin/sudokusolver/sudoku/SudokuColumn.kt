package me.kivalin.sudokusolver.sudoku

class SudokuColumn(grid: SudokuGrid, coord: SudokuCoord) : SudokuHouse(grid, coord) {
    override fun contains(cell: SudokuCell): Boolean {
        return cell.coord.x == coord.x
                && coord.y <= cell.coord.y && cell.coord.y < coord.y + grid.config.size
    }
}
