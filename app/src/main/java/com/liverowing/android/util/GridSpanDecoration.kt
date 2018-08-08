package com.liverowing.android.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

internal class GridSpanDecoration(private val padding: Int) : RecyclerView.ItemDecoration() {

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
                outRect.left = padding
            } else {
                outRect.left = padding / 2
            }

            // If spanIndex + spanSize equals spanCount (it occupies the last column) you apply the full offset on the end, else only half.
            if (spanIndex + spanSize == spanCount) {
                outRect.right = padding
            } else {
                outRect.right = padding / 2
            }

            if (position < spanCount) {
                outRect.top = padding
            } else {
                outRect.top = padding / 2
            }

            outRect.bottom = padding / 2
        } else {
            outRect.top = padding / 2
            outRect.left = padding / 2
            outRect.bottom = padding
            outRect.right = padding
        }
    }
}