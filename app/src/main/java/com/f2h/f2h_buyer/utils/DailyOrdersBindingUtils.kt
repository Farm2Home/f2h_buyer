package com.f2h.f2h_buyer.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
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
    var orderedString = String.format("%s  %s",data.orderedQuantity, data.orderUom)
    text = orderedString
}


@BindingAdapter("discountFormatted")
fun TextView.setDiscountFormatted(data: DailyOrdersUiModel){
    if (data.discountAmount > 0) {
        text = String.format("Discount  ₹%.0f", data.discountAmount)
    } else {
        text = ""
    }
}



@BindingAdapter("totalPriceFormatted")
fun TextView.setTotalPriceFormatted(data: DailyOrdersUiModel){

    if(data.orderAmount <= 0) {
        text = ""
        return
    }

    if (data.discountAmount > 0) {
        val markupPrice = String.format("₹%.0f", data.orderAmount + data.discountAmount)
        val payableString = String.format("Payable  %s ₹%.0f %s", markupPrice, data.orderAmount, data.paymentStatus)
        val spannableString = SpannableString(payableString)
        spannableString.setSpan(StrikethroughSpan(),9,10+markupPrice.length,0)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#dbdbdb")),9,10+markupPrice.length,0)

        text = spannableString
    } else {
        text = String.format("Payable  ₹%.0f  %s", data.orderAmount, data.paymentStatus)
    }
}

@BindingAdapter("totalAmountFormatted")
fun TextView.setTotalAmountFormatted(list: List<DailyOrdersUiModel>?){
    if (list != null) {
        var totalAmount = (0).toFloat()
        list.forEach { element ->
            totalAmount += (element.orderAmount)
        }
        text = String.format("₹%.0f", totalAmount)
    }
}


@BindingAdapter("statusFormatted")
fun TextView.setStatusFormatted(data: DailyOrdersUiModel){

    var displayedStatus: String = data.orderStatus

    if (data.deliveryStatus.equals("DELIVERY_STARTED") ||
        data.deliveryStatus.equals("DELIVERED")){
        displayedStatus = data.deliveryStatus
    }

    val colouredText = SpannableString(displayedStatus)
    var color = Color.DKGRAY
    when (displayedStatus) {
        "ORDERED" -> color = Color.parseColor("#FF9800")
        "CONFIRMED" -> color = Color.parseColor("#FF9800")
        "REJECTED" -> color = Color.parseColor("#F44336")
        "DELIVERED" -> color = Color.parseColor("#4CAF50")
    }
    colouredText.setSpan(ForegroundColorSpan(color),0, displayedStatus.length,0)

    text = colouredText
}


@BindingAdapter("buttonVisibilityFormatted")
fun Button.setButtonVisibilityFormatted(data: DailyOrdersUiModel){
    if (data.isFreezed.equals(false) &&
        data.availableQuantity > 0 &&
        (data.orderStatus.equals("ORDERED") ||
        data.orderStatus.isBlank())){
        visibility = View.VISIBLE
    } else{
        visibility = View.INVISIBLE
    }
}
