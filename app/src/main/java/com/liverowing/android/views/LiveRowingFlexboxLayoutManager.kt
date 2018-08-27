package com.liverowing.android.views

import android.content.Context
import com.google.android.flexbox.FlexboxLayoutManager

class LiveRowingFlexboxLayoutManager(context: Context): FlexboxLayoutManager(context) {
    var isScrollEnabled = true

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollEnabled && super.canScrollHorizontally()
    }
}