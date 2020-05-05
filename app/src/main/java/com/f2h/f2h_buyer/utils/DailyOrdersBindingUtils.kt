package com.f2h.f2h_buyer.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
    var freezeString = ""

    if (isOrderFreezed(data) && "ORDERED".equals(data.orderStatus)){
        freezeString = "\nFreeze"
    }

    var orderedString = String.format("%s  %s%s", getFormattedQtyNumber(data.orderedQuantity), data.orderUom, freezeString)
    if (data.orderStatus.equals("CONFIRMED")){
        orderedString = String.format("%s  %s%s",getFormattedQtyNumber(data.confirmedQuantity), data.orderUom, freezeString)
    }

    text = orderedString
}

private fun getFormattedQtyNumber(number: Double): String {
    return if (number.compareTo(number.toLong()) == 0)
        String.format("%d", number.toLong())
    else
        String.format("%.2f", number)
}


@BindingAdapter("discountFormatted")
fun TextView.setDiscountFormatted(data: DailyOrdersUiModel){
    if (data.discountAmount > 0) {
        text = String.format("Discount  ₹%.0f", data.discountAmount)
    } else {
        text = ""
    }
}


@BindingAdapter("commentFormatted")
fun TextView.setCommentFormatted(data: DailyOrdersUiModel){
    var comment = data.orderComment
    if(data.deliveryStatus.equals("DELIVERED")){
        comment = data.deliveryComment
    }

    text = comment
}



@BindingAdapter("totalPriceFormatted")
fun TextView.setTotalPriceFormatted(data: DailyOrdersUiModel){

    if(data.orderAmount <= 0) {
        text = ""
        return
    }

    var markupPrice = ""
    if (data.discountAmount > 0) {
        markupPrice = String.format("₹%.0f", data.orderAmount + data.discountAmount)
    }

    val payableString = String.format("Payable  %s ₹%.0f \n%s", markupPrice, data.orderAmount, data.paymentStatus)
    val payableStringFormatted = SpannableString(payableString)
    payableStringFormatted.setSpan(StrikethroughSpan(),9,10+markupPrice.length,0)
    payableStringFormatted.setSpan(ForegroundColorSpan(Color.parseColor("#dbdbdb")),9,10+markupPrice.length,0)
    payableStringFormatted.setSpan(RelativeSizeSpan(0.6F), payableString.length-data.paymentStatus.length, payableString.length,0)

    text = payableStringFormatted
}



@BindingAdapter("totalAmountFormatted")
fun TextView.setTotalAmountFormatted(list: List<DailyOrdersUiModel>?){
    if (list != null) {
        var totalAmount = (0).toDouble()
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
    if (isOrderFreezed(data)){
        isEnabled = false
    } else{
        isEnabled = true
    }
}


private fun isOrderFreezed(data: DailyOrdersUiModel) : Boolean {
    if (data.isFreezed.equals(false) &&
       (data.orderStatus.equals("ORDERED") ||
        data.orderStatus.isBlank())){
        return false
    }
    return true
}