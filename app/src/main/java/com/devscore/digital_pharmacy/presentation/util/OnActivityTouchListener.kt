package com.devscore.digital_pharmacy.presentation.util

import android.view.MotionEvent


public interface OnActivityTouchListener {
    fun getTouchCoordinates(ev: MotionEvent?)
}