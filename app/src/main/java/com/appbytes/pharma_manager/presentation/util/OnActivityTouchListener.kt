package com.appbytes.pharma_manager.presentation.util

import android.view.MotionEvent


public interface OnActivityTouchListener {
    fun getTouchCoordinates(ev: MotionEvent?)
}