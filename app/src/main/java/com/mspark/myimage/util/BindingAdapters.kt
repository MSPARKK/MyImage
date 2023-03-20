package com.mspark.myimage.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mspark.myimage.R

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("glide")
    fun image(imageView: ImageView, url: String?) {
        Glide.with(imageView.context)
            .load(url?: "")
            .placeholder(R.color.gray)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("toVisibility")
    fun toVisibility(view: View, visibility: Boolean) {
        view.visibility = if(visibility) View.VISIBLE else View.GONE
    }
}