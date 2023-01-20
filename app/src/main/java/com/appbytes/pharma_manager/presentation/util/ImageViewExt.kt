package com.appbytes.pharma_manager.presentation.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.appbytes.pharma_manager.R

import com.bumptech.glide.request.RequestOptions




const val CROSS_FADE_DURATION = 350

fun ImageView.loadPhotoUrlWithThumbnail(
    url: String,
    color: String?,
    centerCrop: Boolean = false,
    requestListener: RequestListener<Drawable>? = null
) {
    val options = RequestOptions()
        .centerCrop()
        .placeholder(R.mipmap.ic_launcher_round)
        .error(R.drawable.paracetamol)
    color?.let { background = ColorDrawable(Color.parseColor(it)) }
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .addListener(requestListener)
        .apply(options)
        .into(this)
}

//fun AspectRatioImageView.setAspectRatio(width: Int?, height: Int?) {
//    if (width != null && height != null) {
//        aspectRatio = height.toDouble() / width.toDouble()
//    }
//}