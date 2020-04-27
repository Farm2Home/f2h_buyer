package com.f2h.f2h_buyer.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.screens.group.pre_order.PreOrderModel


@BindingAdapter("quantityFormattedTable")
fun TextView.setPriceFormattedFromItem(data: PreOrderModel?){
    data?.let {
        if (data.orderedQuantity > 0) {
            text = String.format("%s %s", data.orderedQuantity, data.orderUom)
        } else {
            text = "0"
        }
    }
}
