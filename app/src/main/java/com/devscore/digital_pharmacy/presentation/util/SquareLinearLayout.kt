package com.devscore.digital_pharmacy.presentation.util

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout


class SquareLinearLayout : LinearLayout {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            widthMeasureSpec
        ) // This is the key that will make the height equivalent to its width
    }
}