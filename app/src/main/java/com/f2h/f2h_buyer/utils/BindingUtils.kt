package com.f2h.f2h_buyer.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.network.models.Item


@BindingAdapter("priceFormattedFromItem")
fun TextView.setPriceFormattedFromItem(data: Item?){
    data?.let {
        text = "\u20B9 " + String.format("%.0f", data.pricePerUnit) + "/" + data.uom
    }
}


@BindingAdapter("customImageFormatted")
fun ImageView.setCustomImageFormatted(itemId: Long){
    var wvItems = arrayListOf<Long>(138,139,153,154,1065,1067,1069)
    var wvTonedMilk = arrayListOf<Long>(147,1166,1168,1170)

    if (wvItems.contains(itemId)) {
        setImageResource(R.drawable.wv_milk)
        return
    }

    if (wvTonedMilk.contains(itemId)) {
        setImageResource(R.drawable.toned_milk)
        return
    }

    setImageResource(R.drawable.f2h_logo)
}