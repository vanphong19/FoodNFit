package com.vanphong.foodnfit.component

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpacingItemDecoration(
    private val spanCount: Int, //số cột
    private val spacing: Int, //khoảng cách
    private val includeEdge: Boolean): ItemDecoration() { //có tính khoảng cách viền hay ko
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if(includeEdge){
            when(column){
                0 ->{
                    outRect.left = 0
                    outRect.right = spacing / 2
                }
                spanCount - 1 ->{
                    outRect.left = spacing / 2
                    outRect.right = 0
                }
                else ->{
                    outRect.left = spacing / 2
                    outRect.right = spacing / 2
                }
            }

            if(position < spanCount){
                outRect.top = spacing
            }
            outRect.bottom = spacing
        }
        else{
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if(position >= spanCount){
                outRect.top = spacing
            }
        }
    }
    }