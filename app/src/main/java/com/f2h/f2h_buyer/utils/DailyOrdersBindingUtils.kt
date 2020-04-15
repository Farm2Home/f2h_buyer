package com.f2h.f2h_buyer.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.screens.group.daily_orders.DailyOrdersModel
import kotlinx.android.synthetic.main.list_ordered_items.view.*
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(data: DailyOrdersModel?){
    data?.let {
        text = "₹ " + String.format("%.0f", data.price) + "/" + data.itemUom
    }
}


@BindingAdapter("orderedQuantityFormatted")
fun TextView.setOrderedQuantityFormatted(data: DailyOrdersModel){
    text = String.format("%s  %s",data.orderedQuantity, data.orderUom)
}


@BindingAdapter("discountFormatted")
fun TextView.setDiscountFormatted(data: DailyOrdersModel){
    if (data.discountAmount > 0) {
        text = String.format("Discount = ₹%.0f", data.discountAmount)
    } else {
        text = ""
    }
}



@BindingAdapter("totalPriceFormatted")
fun TextView.setTotalPriceFormatted(data: DailyOrdersModel){
    if(data.orderAmount > 0) {

        val markupPrice = String.format("₹%.0f", data.orderAmount + data.discountAmount)
        val content = String.format("Payable = %s ₹%.0f", markupPrice, data.orderAmount)
        val spannableString = SpannableString(content)
        spannableString.setSpan(StrikethroughSpan(),10,10+markupPrice.length,0)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#dbdbdb")),10,10+markupPrice.length,0)

        if (data.discountAmount > 0) {
            text = spannableString
        } else {
            text = String.format("Payable = ₹%.0f", data.orderAmount)
        }

    } else{
        text = ""
    }
}

@BindingAdapter("totalAmountFormatted")
fun TextView.setTotalAmountFormatted(list: List<DailyOrdersModel>?){
    if (list != null) {
        var totalAmount = (0).toFloat()
        list.forEach { element ->
            totalAmount += (element.orderAmount)
        }
        text = String.format("Total = ₹ %.0f", totalAmount)
    }
}


@BindingAdapter("statusFormatted")
fun TextView.setStatusFormatted(data: DailyOrdersModel){

    var firstStatus: String = data.orderStatus

    if (data.deliveryStatus.equals("DELIVERY_STARTED") ||
        data.deliveryStatus.equals("DELIVERED")){
        firstStatus = data.deliveryStatus
    }

    text = String.format("%s\n%s", firstStatus, data.paymentStatus)
}


@BindingAdapter("buttonVisibilityFormatted")
fun Button.setButtonVisibilityFormatted(data: DailyOrdersModel){
    if (data.isFreezed.equals(false)){
        visibility = View.VISIBLE
    } else{
        visibility = View.INVISIBLE
    }
}
