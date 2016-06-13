package me.kivalin.sudokusolver

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import me.kivalin.sudokusolver.sudoku.SudokuCell
import me.kivalin.sudokusolver.sudoku.SudokuGrid

class SudokuDigitSelectorView(context: Context, attrs: AttributeSet) :
        ViewGroup(context, attrs), SudokuChildView {

    private var touching = false
    private var visible = false
    private var passedTouchSlop = false
    private var downX = 0f
    private var downY = 0f
    private var currentCellX = 0
    private var currentCellY = 0
    private var currentValue = 0

    override fun onDimensionsChanged() {
    }

    override fun onValuesChanged() {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val spec = MeasureSpec.makeMeasureSpec(fromDp(32f).toInt(), MeasureSpec.EXACTLY)
        children.filter { it.visibility != GONE }
                .forEach { it.measure(spec, spec) }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        children.filter { it.visibility != GONE }
                .forEach {
                    val w = it.measuredWidth
                    val h = it.measuredHeight
                    it.layout(0, 0, w, h)
                    it.pivotX = w / 2f
                    it.pivotY = h / 2f
                }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val parent = parent as? SudokuView
        val grid = parent?.grid
        val dimensions = parent?.dimensions
        if (event != null && grid != null && dimensions != null) {
            handleTouchEvent(event, grid, dimensions)
        }
        return true
    }

    private fun handleTouchEvent(
            event: MotionEvent, grid: SudokuGrid, dimensions: SudokuDimensions) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                passedTouchSlop = false
                currentCellX = (event.x / dimensions.cellSizePx).toInt()
                currentCellY = (event.y / dimensions.cellSizePx).toInt()
                currentValue = -1
                grid.cellAt(currentCellX, currentCellY)?.let { cell ->
                    touching = true
                    visible = true
                    expand(grid, cell, dimensions)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!passedTouchSlop) {
                    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
                    if (Math.abs(event.x - downX) > touchSlop
                            || Math.abs(event.y - downY) > touchSlop) {
                        passedTouchSlop = true
                    }
                }
                if (!passedTouchSlop || !touching) {
                    return
                }
                val size = grid.config.size
                val originX = (currentCellX + 0.5f) * dimensions.cellSizePx
                val originY = (currentCellY + 0.5f) * dimensions.cellSizePx
                val dx = (event.x - originX).toDouble()
                val dy = (event.y - originY).toDouble()
                val dst = 2 * Math.PI / (size + 1)
                val phi = (2 * Math.PI + Math.atan2(dy, dx)).mod(2 * Math.PI)
                val value = (0..size)
                        .filter { getChildAt(it).isEnabled }
                        .map { Pair(it, angle(it * dst - Math.PI / 2, phi)) }
                        .filter { it.second < Math.PI / 4 }
                        .minBy { it.second }
                        ?.first ?: -1
                if (currentValue != value) {
                    currentValue = value
                    selectValue(value)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (touching) {
                    touching = false
                    collapse(dimensions, currentValue)
                    if (currentValue != -1) {
                        grid.cellAt(currentCellX, currentCellY)?.let { cell ->
                            (parent as? SudokuView)?.listener
                                    ?.onSelectValue(cell, currentValue)
                        }
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                if (touching) {
                    touching = false
                    collapse(dimensions, -1)
                }
            }
        }
    }

    private fun angle(a: Double, b: Double): Double {
        return Math.min(Math.abs(a - b), Math.abs(2 * Math.PI + a - b))
    }

    private fun expand(grid: SudokuGrid, cell: SudokuCell, dimensions: SudokuDimensions) {
        val radius = fromDp(50f)
        val size = grid.config.size
        val baseX = (cell.coord.x + 0.5f) * dimensions.cellSizePx - fromDp(16f)
        val baseY = (cell.coord.y + 0.5f) * dimensions.cellSizePx - fromDp(16f)

        (childCount..size).map { createLabel(it) }.forEach { addView(it) }

        val candidates = cell.candidates
        val dst = 2 * Math.PI / (size + 1)

        children.forEachIndexed { value, view ->
            val phi = value * dst - Math.PI / 2
            val x = radius * Math.cos(phi).toFloat()
            val y = radius * Math.sin(phi).toFloat()
            view.visibility = if (value <= size) VISIBLE else GONE
            view.isEnabled = value == 0 || candidates and (1 shl value) != 0
            view.alpha = if (view.isEnabled) 1f else 0.2f
            view.x = baseX
            view.y = baseY
            view.scaleX = 1f
            view.scaleY = 1f
            view.animate().cancel()
            view.animate()
                    .xBy(x)
                    .yBy(y)
                    .start()
        }
    }

    private fun selectValue(value: Int) {
        children.forEachIndexed { index, view ->
            val scale = if (index == value) 1.2f else 0.8f
            view.animate().scaleX(scale).scaleY(scale).start()
        }
    }

    private fun collapse(dimensions: SudokuDimensions, value: Int) {
        val baseX = (currentCellX + 0.5f) * dimensions.cellSizePx - fromDp(16f)
        val baseY = (currentCellY + 0.5f) * dimensions.cellSizePx - fromDp(16f)
        children.forEachIndexed { index, view ->
            if (index != 0 && index == value) {
                view.animate().scaleX(1f).scaleY(1f)
                        .x(baseX).y(baseY)
                        .start()
            } else {
                view.animate().scaleX(0f).scaleY(0f).start()
            }
        }
        visible = false
    }

    private fun createLabel(value: Int): View {
        val background = ShapeDrawable(OvalShape())
        background.paint.color = Color.WHITE
        val view = TextView(context)
        view.setTextColor(Color.BLACK)
        view.background = background
        view.elevation = fromDp(2f)
        view.textSize = 16f
        view.text = if (value == 0) "×" else value.toString()
        view.gravity = Gravity.CENTER
        val animator = view.animate()
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!visible) {
                    view.visibility = GONE
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        return view
    }
}
