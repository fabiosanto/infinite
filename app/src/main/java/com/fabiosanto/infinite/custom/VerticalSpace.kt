package com.fabiosanto.infinite.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fabiosanto.infinite.utils.px

class VerticalSpace : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = 8.px
    }
}