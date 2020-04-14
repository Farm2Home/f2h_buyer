package com.f2h.f2h_buyer.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.screens.group.daily_orders.DailyOrdersModel
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(data: DailyOrdersModel?){
    data?.let {
        text = "\u20B9 " + String.format("%.0f", data.price) + "/" + data.itemUom
    }
}


@BindingAdapter("orderedQuantityFormatted")
fun TextView.setOrderedQuantityFormatted(data: DailyOrdersModel){
    text = String.format("%s  %s",data.orderedQuantity, data.orderUom)
}


@BindingAdapter("totalPriceFormatted")
fun TextView.setTotalPriceFormatted(data: DailyOrdersModel){
    if(data.orderAmount > 0) {
        text = String.format(
            "Total = ₹%.0f (₹%.0f - ₹%.0f)",
            data.orderAmount - data.discountAmount,
            data.orderAmount,
            data.discountAmount
        )
    } else{
        text = ""
    }
}
