package me.kivalin.sudokusolver.sudoku

class SudokuBox(grid: SudokuGrid, coord: SudokuCoord) : SudokuHouse(grid, coord) {
    override fun contains(cell: SudokuCell): Boolean {
        return coord.x <= cell.coord.x && cell.coord.x < coord.x + grid.config.boxWidth
                && coord.y <= cell.coord.y && cell.coord.y < coord.y + grid.config.boxHeight
    }
}
