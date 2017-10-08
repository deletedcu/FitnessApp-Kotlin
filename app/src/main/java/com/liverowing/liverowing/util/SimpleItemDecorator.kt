package com.liverowing.liverowing.util

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


/**
 * Created by henrikmalmberg on 2017-10-08.
 */
class SimpleItemDecorator : RecyclerView.ItemDecoration {

    internal var space: Int = 0
    internal var isHorizontalLayout: Boolean = false

    constructor(space: Int) {
        this.space = space
    }

    constructor(space: Int, isHorizontalLayout: Boolean) {
        this.space = space
        this.isHorizontalLayout = isHorizontalLayout
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (isHorizontalLayout) {
            outRect.bottom = space
            outRect.right = space
            outRect.left = space
            outRect.top = space

        } else {
            outRect.bottom = space
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = space
            else
                outRect.top = 0
        }
    }
}