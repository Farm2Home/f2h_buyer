package com.f2h.f2h_buyer.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.network.models.Item
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("availableDateFormatted")
fun TextView.setAvailableDateFormatted(item: Item?){
    item?.let {
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formatter: DateFormat = SimpleDateFormat("MMM-dd-yyy")
        var formattedDate: String = ""

        try {
            var date = item.itemAvailability.get(0).availableDate
            formattedDate = formatter.format(parser.parse(date))
        }catch (e: Exception){
            println(e)
        }

        text = "Next Available - " + formattedDate
    }
}


@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(item: Item?){
    item?.let {
        text = "\u20B9 " + String.format("%.0f", item.pricePerUnit) + "/" + item.uom
    }
}
