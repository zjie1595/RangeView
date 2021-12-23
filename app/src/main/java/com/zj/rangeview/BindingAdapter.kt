package com.zj.rangeview

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("bitmap")
fun loadImage(view: ImageView, bitmap: Bitmap) {
    view.setImageBitmap(bitmap)
}