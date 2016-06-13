package me.kivalin.sudokusolver

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import me.kivalin.sudokusolver.sudoku.SudokuCoord
import me.kivalin.sudokusolver.sudoku.SudokuGrid

class SudokuLabelsView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private val labelPaint = TextPaint()

    init {
        labelPaint.textSize = fromSp(16f)
        labelPaint.color = Color.BLACK
        labelPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val cellSize = (parent as? SudokuView)?.dimensions?.cellSizePx ?: 0
        val spec = MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY)
        children.filter { it.visibility != GONE }
                .forEach { it.measure(spec, spec) }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        children.filter { it.visibility != GONE }
                .forEach { it.layout(0, 0, it.measuredWidth, it.measuredHeight) }
    }

    internal fun update(grid: SudokuGrid?, dimensions: SudokuDimensions?) {
        val labels = grid?.cells.orEmpty().filter { it.value != 0 }.associateBy { it.coord }
        val children = children.map { it as TextView }.associateBy { it.tag as SudokuCoord }
        val update = labels.keys.intersect(children.keys).map { Pair(labels[it]!!, children[it]!!) }
        val show = labels.keys.minus(children.keys).mapNotNull { labels[it] }
        val hide = children.keys.minus(labels.keys).mapNotNull { children[it] }
        val cellSize = dimensions?.cellSizePx?.toFloat() ?: 0f
        update.forEach {
            val (cell, view) = it
            view.visibility = VISIBLE
            view.text = cell.value.toString()
            view.x = cell.coord.x * cellSize
            view.y = cell.coord.y * cellSize
        }
        show.zip(hide).forEach {
            val (cell, view) = it
            view.tag = cell.coord
            view.text = cell.value.toString()
            view.visibility = VISIBLE
            view.x = cell.coord.x * cellSize
            view.y = cell.coord.y * cellSize
            view.alpha = 0f
            view.animate().cancel()
            view.animate().alpha(1f).start()
        }
        show.drop(Math.min(show.size, hide.size)).forEach {
            val view = TextView(context)
            view.tag = it.coord
            view.text = it.value.toString()
            view.visibility = VISIBLE
            view.gravity = Gravity.CENTER
            view.textSize = 16f
            view.setTextColor(Color.BLACK)
            view.x = it.coord.x * cellSize
            view.y = it.coord.y * cellSize
            val animator = view.animate()
            animator.startDelay = 300
            animator.duration = 0
            addView(view)
            view.alpha = 0f
            view.animate().alpha(1f).start()
        }
        hide.drop(Math.min(show.size, hide.size)).forEach {
            it.visibility = GONE
        }
    }
}
