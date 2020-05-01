package com.f2h.f2h_buyer.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.screens.group.pre_order.PreOrderItemsModel
import com.f2h.f2h_buyer.screens.group.pre_order.PreOrderUiModel
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("priceFormattedPreOrder")
fun TextView.setPriceFormattedFromPreOrderUiModel(data: PreOrderUiModel?){
    data?.let {
        text = String.format("%s %s", data.itemPrice, data.itemUom)
    }
}

@BindingAdapter("dateFormattedPreOrderItems")
fun TextView.setDateFormattedPreOrderItems(data: PreOrderItemsModel?){
    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    val df_out: DateFormat = SimpleDateFormat("MMM\ndd\nEEE")
    data?.let {
        var date: String = df_out.format(df.parse(data.availableDate))
        text = String.format("%s", date)
    }
}

