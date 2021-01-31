package com.f2h.f2h_buyer.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_CONFIRMED
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_DELIVERED
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_ORDERED
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_REJECTED
import com.f2h.f2h_buyer.constants.F2HConstants.PAYMENT_STATUS_PAID
import com.f2h.f2h_buyer.constants.F2HConstants.PAYMENT_STATUS_PENDING
import com.f2h.f2h_buyer.screens.group.daily_orders.DailyOrderHeaderUiModel
import com.f2h.f2h_buyer.screens.group.daily_orders.DailyOrders
import com.f2h.f2h_buyer.screens.group.daily_orders.ServiceOrder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter(value = ["headerDateFormatter", "headerAmountFormatter", "currency"])
fun TextView.setHeaderFormatted(date: String?, amount: Double?, currency: String?){
    date?.let {
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formatter: DateFormat = SimpleDateFormat("EEEE dd-MMMM")
        text = "Delivery on " + formatter.format(parser.parse(date)) + ", Total " + String.format("%s%.0f", currency, amount)
    }
}

@BindingAdapter("packingNumberFormatter")
fun TextView.setPackingNumberFormatter(packingNumber: Long?){
    packingNumber?.let {
        text = "Packing Number : " + String.format("%d", packingNumber)
    }
}

@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(data: DailyOrders){
    data?.let {
        text = String.format("%s %.0f",data.currency, data.price) + "/" + data.itemUom
    }
}

@BindingAdapter("servicePriceFormatted")
fun TextView.setServicePriceFormatted(data: ServiceOrder?){
    data?.let {
        text = String.format(" %s %.0f", data.currency, data.amount)
    }
}


@BindingAdapter("orderedQuantityFormatted")
fun TextView.setOrderedQuantityFormatted(data: DailyOrders){
    var freezeString = ""

    if (isFreezeStringDisplayed(data)){
        freezeString = "\nFreeze"
    }

    // Default behaviour, show ordered quantity
    var orderedString = String.format("%s  %s", getFormattedQtyNumber(data.orderedQuantity), freezeString)
    if (data.orderStatus.equals(ORDER_STATUS_CONFIRMED) ||
        data.orderStatus.equals(ORDER_STATUS_DELIVERED)){
        orderedString = String.format("%s  %s",getFormattedQtyNumber(data.confirmedQuantity), freezeString)
    }
    text = orderedString
}

private fun isFreezeStringDisplayed(data: DailyOrders) =
    !isChangeQuantityButtonsEnabled(data) && (ORDER_STATUS_ORDERED.equals(data.orderStatus) || data.orderStatus.isBlank())

private fun getFormattedQtyNumber(number: Double): String {
    if (number == null) return ""
    return if (number.compareTo(number.toLong()) == 0)
        String.format("%d", number.toLong())
    else
        String.format("%.2f", number)
}


@BindingAdapter("discountFormatted")
fun TextView.setDiscountFormatted(data: DailyOrders){
    if (data.discountAmount > 0) {
        text = String.format("Discount  %s%.0f", data.currency, data.discountAmount)
    } else {
        text = ""
    }
}


@BindingAdapter("commentFormatted")
fun TextView.setCommentFormatted(data: DailyOrders){
    val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    val formatter: DateFormat = SimpleDateFormat("dd-MMMM, hh:mm a")
    var displayText = ""
    data.comments.sortByDescending { comment -> parser.parse(comment.createdAt) }
    data.comments.forEach { comment ->
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        var date = formatter.format(parser.parse(comment.createdAt))
        displayText = String.format("%s%s : %s - %s\n\n", displayText, date, comment.commenter, comment.comment)
    }
    text = displayText
}


@BindingAdapter("moreDetailsLayoutFormatted")
fun ConstraintLayout.setMoreDetailsLayoutFormatted(data: DailyOrders){
    if(data.isMoreDetailsDisplayed){
        visibility = View.VISIBLE
        return
    }
    visibility = View.GONE
}



@BindingAdapter("totalPriceFormatted")
fun TextView.setTotalPriceFormatted(data: DailyOrders){

    if(data.orderAmount <= 0) {
        text = ""
        return
    }

    var markupPrice = ""
    if (data.discountAmount > 0) {
        markupPrice = String.format("%s%.0f",data.currency, data.orderAmount + data.discountAmount)
    }

    val payableString = String.format("Payable  %s %s%.0f \n%s", markupPrice, data.currency, data.orderAmount, data.paymentStatus)
    val payableStringFormatted = SpannableString(payableString)
    payableStringFormatted.setSpan(StrikethroughSpan(),9,10+markupPrice.length,0)
    payableStringFormatted.setSpan(ForegroundColorSpan(Color.parseColor("#dbdbdb")),9,10+markupPrice.length,0)
    payableStringFormatted.setSpan(RelativeSizeSpan(0.6F), payableString.length-data.paymentStatus.length, payableString.length,0)

    //Make PAID Green colour
    if(data.paymentStatus.equals(PAYMENT_STATUS_PAID)) {
        payableStringFormatted.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context,R.color.green_status)),
            payableString.length - data.paymentStatus.length,
            payableString.length,
            0
        )
    }

    //Make PENDING RED colour
    if(data.paymentStatus.equals(PAYMENT_STATUS_PENDING)) {
        payableStringFormatted.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context,R.color.red_status)),
            payableString.length - data.paymentStatus.length,
            payableString.length,
            0
        )
    }


    text = payableStringFormatted
}



@BindingAdapter("totalAmountFormatted")
fun TextView.setTotalAmountFormatted(list: List<DailyOrders>?){
    if (list != null) {
        var totalAmount = (0).toDouble()
        var currency = ""
        list.forEach { element ->
            currency = element.currency
            totalAmount += (element.orderAmount)
        }
        text = String.format("%s%.0f", currency, totalAmount)
    }
}


@BindingAdapter("textVisibility")
fun TextView.setNoOrdersVisibility(list: List<DailyOrderHeaderUiModel>?){
    if (list.isNullOrEmpty()) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}


@BindingAdapter("statusFormatted")
fun TextView.setStatusFormatted(data: DailyOrders){

    var displayedStatus: String = data.orderStatus

    if (data.orderStatus.equals(ORDER_STATUS_CONFIRMED)){
        displayedStatus = data.orderStatus
    }

    val colouredText = SpannableString(displayedStatus)
    var color = Color.DKGRAY
    when (displayedStatus) {
        ORDER_STATUS_ORDERED -> color = ContextCompat.getColor(context, R.color.orange_status)
        ORDER_STATUS_CONFIRMED -> color = ContextCompat.getColor(context, R.color.orange_status)
        ORDER_STATUS_REJECTED -> color = ContextCompat.getColor(context, R.color.red_status)
        ORDER_STATUS_DELIVERED -> color = ContextCompat.getColor(context, R.color.green_status)
    }
    colouredText.setSpan(ForegroundColorSpan(color),0, displayedStatus.length,0)

    text = colouredText
}



@BindingAdapter("buttonVisibilityFormatted")
fun Button.setButtonVisibilityFormatted(data: DailyOrders){
    if(isChangeQuantityButtonsEnabled(data)){
        isEnabled = true
        visibility = View.VISIBLE
    } else {
        isEnabled = false
        visibility = View.INVISIBLE
    }
}


private fun isChangeQuantityButtonsEnabled(data: DailyOrders) : Boolean {
    if (data.isFreezed.equals(false) &&
        (data.orderStatus.equals(ORDER_STATUS_ORDERED) ||
                data.orderStatus.isBlank())){
        return true
    }
    return false
}