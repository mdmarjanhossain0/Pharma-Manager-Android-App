package com.devscore.digital_pharmacy.presentation.util

import android.content.Context
import android.util.AttributeSet
import android.view.View.getDefaultSize
import android.widget.ImageView


class SquareImageView : ImageView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec)
        setMeasuredDimension(width, width)
    }

    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, w, oldw, oldh)
    }
}