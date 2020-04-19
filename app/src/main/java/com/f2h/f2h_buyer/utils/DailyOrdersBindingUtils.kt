package com.f2h.f2h_buyer.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.screens.group.daily_orders.DailyOrdersUiModel


@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(data: DailyOrdersUiModel?){
    data?.let {
        text = "₹ " + String.format("%.0f", data.price) + "/" + data.itemUom
    }
}


@BindingAdapter("orderedQuantityFormatted")
fun TextView.setOrderedQuantityFormatted(data: DailyOrdersUiModel){
    text = String.format("%s  %s",data.orderedQuantity, data.orderUom)
}


@BindingAdapter("discountFormatted")
fun TextView.setDiscountFormatted(data: DailyOrdersUiModel){
    if (data.discountAmount > 0) {
        text = String.format("Discount = ₹%.0f", data.discountAmount)
    } else {
        text = ""
    }
}



@BindingAdapter("totalPriceFormatted")
fun TextView.setTotalPriceFormatted(data: DailyOrdersUiModel){
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
fun TextView.setTotalAmountFormatted(list: List<DailyOrdersUiModel>?){
    if (list != null) {
        var totalAmount = (0).toFloat()
        list.forEach { element ->
            if (!element.orderStatus.equals("REJECTED") &&
                !element.paymentStatus.equals("PAID"))
            totalAmount += (element.orderAmount)
        }
        text = String.format("Total = ₹ %.0f", totalAmount)
    }
}


@BindingAdapter("statusFormatted")
fun TextView.setStatusFormatted(data: DailyOrdersUiModel){

    var firstStatus: String = data.orderStatus

    if (data.deliveryStatus.equals("DELIVERY_STARTED") ||
        data.deliveryStatus.equals("DELIVERED")){
        firstStatus = data.deliveryStatus
    }

    text = String.format("%s\n%s", firstStatus, data.paymentStatus)
}


@BindingAdapter("buttonVisibilityFormatted")
fun Button.setButtonVisibilityFormatted(data: DailyOrdersUiModel){
    if (data.isFreezed.equals(false) &&
        data.orderStatus.equals("ORDERED") ||
        data.orderStatus.isBlank()){
        visibility = View.VISIBLE
    } else{
        visibility = View.INVISIBLE
    }
}
