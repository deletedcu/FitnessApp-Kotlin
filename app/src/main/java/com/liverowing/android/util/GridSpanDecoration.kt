package com.liverowing.android.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

internal class GridSpanDecoration : RecyclerView.ItemDecoration {
    
    var paddingLeft: Int = 0
    var paddingTop: Int = 0
    var paddingRight: Int = 0
    var paddingBotton: Int = 0

    constructor(padding: Int) {
        paddingLeft = padding
        paddingTop = padding
        paddingRight = padding
        paddingBotton = padding
    }
    
    constructor(paddingLeft: Int, paddingTop: Int, paddingRight: Int, paddingBotton: Int) {
        this.paddingLeft = paddingLeft
        this.paddingTop = paddingTop
        this.paddingRight = paddingRight
        this.paddingBotton = paddingBotton
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val gridLayoutManager = parent.layoutManager as GridLayoutManager?
        val spanCount = gridLayoutManager!!.spanCount


        val params = view.layoutParams as GridLayoutManager.LayoutParams

        val position = parent.getChildAdapterPosition(view)
        val spanIndex = params.spanIndex
        val spanSize = params.spanSize


        if (spanCount > 1) {
            // If it is in column 0 you apply the full offset on the start side, else only half
            if (spanIndex == 0) {
                outRect.left = paddingLeft
            } else {
                outRect.left = paddingLeft / 2
            }

            // If spanIndex + spanSize equals spanCount (it occupies the last column) you apply the full offset on the end, else only half.
            if (spanIndex + spanSize == spanCount) {
                outRect.right = paddingRight
            } else {
                outRect.right = paddingRight / 2
            }

            if (position < spanCount) {
                outRect.top = paddingTop
            } else {
                outRect.top = paddingTop / 2
            }

            outRect.bottom = paddingBotton / 2
        } else {
            outRect.top = paddingTop / 2
            outRect.left = paddingLeft / 2
            outRect.bottom = paddingBotton
            outRect.right = paddingRight
        }
    }
}