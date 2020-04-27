package com.f2h.f2h_buyer.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.network.models.Item


@BindingAdapter("priceFormattedFromItem")
fun TextView.setPriceFormattedFromItem(data: Item?){
    data?.let {
        text = "\u20B9 " + String.format("%.0f", data.pricePerUnit) + "/" + data.uom
    }
}
