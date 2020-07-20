package com.f2h.f2h_buyer.utils

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.network.models.Item


@BindingAdapter("priceFormattedFromItem")
fun TextView.setPriceFormattedFromItem(data: Item?){
    data?.let {
        text = "\u20B9 " + String.format("%.0f", data.pricePerUnit) + "/" + data.uom
    }
}

@BindingAdapter("progressBarVisibility")
fun ProgressBar.setProgressBarVisibility(isVisible: Boolean){
    if (isVisible){
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@BindingAdapter("buttonStatus")
fun Button.setButtonStatus(status: Boolean){
    isEnabled = status
}


@BindingAdapter("loadSquareRoundedImage")
fun loadSquareRoundedImage(view: ImageView, url: String?) {
    Glide.with(view)
        .load(url ?: "")
        .transform(CenterCrop(), RoundedCorners(25))
        .into(view)
}


@BindingAdapter("loadImage")
fun loadImage(view: ImageView, url: String?) {
    Glide.with(view)
        .load(url ?: "")
        .centerCrop()
        .into(view)
}

@BindingAdapter("loadGroupImage")
fun loadGroupImage(view: ImageView, url: String?) {
    Glide.with(view)
        .load(url ?: "")
        .circleCrop()
        .fallback(R.drawable.main_logo)
        .error(R.drawable.main_logo)
        .into(view)
}
