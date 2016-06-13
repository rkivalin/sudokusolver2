package me.kivalin.sudokusolver

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

val ViewGroup.children: List<View>
    get() = (1..childCount).map { getChildAt(it - 1) }

val View.innerWidth: Int
    get() = width - paddingLeft - paddingRight

val View.innerHeight: Int
    get() = height - paddingTop - paddingBottom

inline fun <reified T> ViewGroup.findChildOfType(): T? {
    return children.find { it is T } as T?
}

fun View.fromDp(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp, resources.displayMetrics)
}

fun View.fromSp(sp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            sp, resources.displayMetrics)
}
