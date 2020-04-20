package com.f2h.f2h_buyer.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.screens.group.pre_order.TableComponent
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("quantityFormattedTable")
fun TextView.setPriceFormattedFromItem(data: TableComponent?){
    data?.let {
        if (data.quantity > 0) {
            text = String.format("%s %s", data.quantity, data.uom)
        } else {
            text = "0"
        }
    }
}
