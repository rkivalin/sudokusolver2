package me.kivalin.sudokusolver

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import me.kivalin.sudokusolver.sudoku.SudokuConfig
import me.kivalin.sudokusolver.sudoku.SudokuGrid

class SudokuView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    var listener: SudokuListener? = null

    var grid: SudokuGrid? = null
        set(value) {
            if (field != value) {
                field = value
                computeSudokuLayout()
            }
        }

    internal var dimensions: SudokuDimensions? = null

    init {
        clipToPadding = false
        clipChildren = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        val innerWidth = width - paddingLeft - paddingRight
        val innerHeight = height - paddingTop - paddingBottom
        children.filter { it.visibility != GONE }
                .forEach {
                    it.measure(MeasureSpec.makeMeasureSpec(innerWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(innerHeight, MeasureSpec.EXACTLY))
                }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = paddingLeft
        val top = paddingTop
        children.filter { it.visibility != GONE }
                .forEach {
                    it.layout(left, top, left + it.measuredWidth, top + it.measuredHeight)
                }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computeSudokuLayout()
    }

    private fun computeSudokuLayout() {
        val dimensionsChanged = updateDimensions()

        if (dimensionsChanged) {
            findChildOfType<SudokuGridView>()?.let { gridView ->
                val cellBorderPath = gridView.cellBorderPath
                val boxBorderPath = gridView.boxBorderPath
                cellBorderPath.reset()
                boxBorderPath.reset()
                dimensions?.let { dimensions ->
                    val boxWidth = dimensions.config.boxWidth
                    val boxHeight = dimensions.config.boxHeight
                    val sudokuSize = dimensions.config.size
                    val cellSize = dimensions.cellSizePx.toFloat()
                    dimensions.config.grids.forEach { coord ->
                        (0..sudokuSize).forEach { i ->
                            with(if (i % boxWidth == 0) boxBorderPath else cellBorderPath) {
                                moveTo((coord.x + i) * cellSize, coord.y * cellSize)
                                lineTo((coord.x + i) * cellSize, (coord.y + sudokuSize) * cellSize)
                            }
                            with(if (i % boxHeight == 0) boxBorderPath else cellBorderPath) {
                                moveTo(coord.x * cellSize, (coord.y + i) * cellSize)
                                lineTo((coord.x + sudokuSize) * cellSize, (coord.y + i) * cellSize)
                            }
                        }
                    }
                }
                gridView.invalidate()
            }
        }

        findChildOfType<SudokuLabelsView>()?.update(grid, dimensions)
    }

    private fun updateDimensions(): Boolean {
        val grid = grid
        val innerWidth = innerWidth
        val innerHeight = innerHeight
        val dimensions = dimensions
        if (grid == null || innerWidth <= 0 || innerHeight <= 0) {
            this.dimensions = null
            return dimensions != null
        } else if (dimensions == null
                || dimensions.viewWidthPx != innerWidth
                || dimensions.viewHeightPx != innerHeight
                || dimensions.config != grid.config) {
            this.dimensions = computeDimensions(grid.config)
            return true
        }
        return false
    }

    private fun computeDimensions(config: SudokuConfig): SudokuDimensions {
        val gridWidth = config.gridWidth
        val gridHeight = config.gridHeight
        val cellSizePx = Math.min(innerWidth / gridWidth, innerHeight / gridHeight)
        return SudokuDimensions(
                viewWidthPx = innerWidth,
                viewHeightPx = innerHeight,
                config = config,
                gridWidth = gridWidth,
                gridHeight = gridHeight,
                cellSizePx = cellSizePx)
    }
}
