package com.f2h.f2h_buyer.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.network.models.Item
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("availableDateFormatted")
fun TextView.setAvailableDateFormatted(item: Item){
    item?.let {
        var date = item.itemAvailability.get(0).availableDate

        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formatter: DateFormat = SimpleDateFormat("MMM-dd-yyy")

        text = "Next Available - " + formatter.format(parser.parse(date))
    }
}


@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(item: Item){
    item?.let {

        text = "\u20B9 " + String.format("%.0f", item.pricePerUnit) + "/" + item.uom
    }
}
