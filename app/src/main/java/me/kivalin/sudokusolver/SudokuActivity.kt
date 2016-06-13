package me.kivalin.sudokusolver

import android.os.Bundle
import android.support.v4.app.FragmentActivity
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
                sudokuView?.grid = sudokuView?.grid?.copyWithValue(cell.coord, value)
            }
        }
    }
}
