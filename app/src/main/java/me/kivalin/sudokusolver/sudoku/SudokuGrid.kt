package me.kivalin.sudokusolver.sudoku

import java.util.*

class SudokuGrid(val config: SudokuConfig, val values: Map<SudokuCoord, Int> = emptyMap()) {
    val cells: List<SudokuCell>
    val rows: List<SudokuRow>
    val columns: List<SudokuColumn>
    val boxes: List<SudokuBox>
    val houses: List<SudokuHouse>

    fun cellAt(x: Int, y: Int): SudokuCell? {
        return cells.find { it.coord.x == x && it.coord.y == y }
    }

    init {
        val range = 0..config.size - 1

        cells = config.grids.flatMap { coord ->
            range.flatMap { x ->
                range.map { y ->
                    val cellCoord = coord.offsetBy(x, y)
                    val value = values[cellCoord] ?: 0
                    SudokuCell(this, cellCoord, value)
                }
            }
        }.distinctBy { it.coord }

        rows = config.grids.flatMap { coord ->
            range.map { y ->
                SudokuRow(this, coord.offsetBy(0, y))
            }
        }.distinctBy { it.coord }

        columns = config.grids.flatMap { coord ->
            range.map { x ->
                SudokuColumn(this, coord.offsetBy(x, 0))
            }
        }.distinctBy { it.coord }

        if (config.boxWidth == 1 || config.boxHeight == 1) {
            boxes = listOf()
        } else {
            boxes = config.grids.flatMap { coord ->
                range.map { i ->
                    val x = config.boxWidth * (i % config.boxWidth)
                    val y = config.boxHeight * (i / config.boxWidth)
                    SudokuBox(this, coord.offsetBy(x, y))
                }
            }.distinctBy { it.coord }
        }

        houses = rows.plus(columns).plus(boxes)
    }

    fun copyWithValue(coord: SudokuCoord, value: Int): SudokuGrid {
        checkValue(value)
        return SudokuGrid(config,
                if (value != 0) values.plus(Pair(coord, value))
                else values.filterKeys { it != coord })
    }

    fun copyWithValues(newValues: Map<SudokuCoord, Int>): SudokuGrid {
        newValues.forEach { checkValue(it.value) }
        return SudokuGrid(config,
                values.plus(newValues.filter { it.value != 0 })
                    .filterKeys { newValues[it] != 0 })
    }

    private fun checkValue(value: Int) {
        if (value < 0 || value > config.size) {
            throw IllegalArgumentException(String.format(Locale.ROOT,
                    "value=%d size=%d", value, config.size));
        }
    }
}
