package me.kivalin.sudokusolver

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import me.kivalin.sudokusolver.solvers.HiddenSinglesSolver
import me.kivalin.sudokusolver.solvers.NakedSinglesSolver
import me.kivalin.sudokusolver.sudoku.SudokuCell
import me.kivalin.sudokusolver.sudoku.SudokuConfig
import me.kivalin.sudokusolver.sudoku.SudokuGrid

class SudokuActivity : FragmentActivity() {
    var sudokuView: SudokuView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sudoku)
        sudokuView = findViewById(R.id.sudoku) as SudokuView
        sudokuView?.grid = SudokuGrid(SudokuConfig(
                boxWidth = 3,
                boxHeight = 3))
        sudokuView?.listener = object : SudokuListener {
            override fun onSelectValue(cell: SudokuCell, value: Int) {
                sudokuView?.grid = sudokuView?.grid?.let { grid ->
                    solve(grid.copyWithValue(cell.coord, value))
                }
            }
        }
    }

    tailrec fun solve(grid: SudokuGrid): SudokuGrid {
        val solved = solveHiddenSingles(solveNakedSingles(grid))
        return if (solved == grid) grid else solve(solved)
    }

    tailrec fun solveNakedSingles(grid: SudokuGrid): SudokuGrid {
        val solved = NakedSinglesSolver(grid).solve()
        return if (solved.isEmpty()) grid else solveNakedSingles(grid.copyWithValues(solved))
    }

    tailrec fun solveHiddenSingles(grid: SudokuGrid): SudokuGrid {
        val solved = HiddenSinglesSolver(grid).solve()
        return if (solved.isEmpty()) grid else solveHiddenSingles(grid.copyWithValues(solved))
    }
}
