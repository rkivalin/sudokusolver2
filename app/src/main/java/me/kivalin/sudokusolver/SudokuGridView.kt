package me.kivalin.sudokusolver

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class SudokuGridView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val cellBorderPaint = Paint()
    private val boxBorderPaint = Paint()

    internal val cellBorderPath = Path()
    internal val boxBorderPath = Path()

    init {
        cellBorderPaint.style = Paint.Style.STROKE
        cellBorderPaint.color = Color.BLACK
        cellBorderPaint.strokeWidth = context.resources.displayMetrics.density

        boxBorderPaint.style = Paint.Style.STROKE
        boxBorderPaint.color = Color.BLACK
        boxBorderPaint.strokeWidth = context.resources.displayMetrics.density * 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            drawPath(cellBorderPath, cellBorderPaint)
            drawPath(boxBorderPath, boxBorderPaint)
        }
    }
}
