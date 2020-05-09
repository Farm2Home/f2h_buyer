package com.f2h.f2h_buyer.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.screens.report.ReportItemsModel
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(data: ReportItemsModel?){
    data?.let {
        text = "₹ " + String.format("%.0f", data.price) + "/" + data.itemUom
    }
}


@BindingAdapter("orderDateFormatted")
fun TextView.setOrderDateFormatted(data: ReportItemsModel?){
    data?.let {
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val df: DateFormat = SimpleDateFormat("dd-MMM-yyyy")
        text = df.format(parser.parse(data.orderedDate))
    }
}


@BindingAdapter("orderedQuantityFormatted")
fun TextView.setOrderedQuantityFormatted(data: ReportItemsModel){
    var freezeString = ""

    if (isOrderFreezed(data) && "ORDERED".equals(data.orderStatus)){
        freezeString = "\nFreeze"
    }

    var orderedString = String.format("%s  %s%s", getFormattedQtyNumber(data.displayQuantity), data.itemUom ,freezeString)

    text = orderedString
}

private fun getFormattedQtyNumber(number: Double): String {
    return if (number.compareTo(number.toLong()) == 0)
        String.format("%d", number.toLong())
    else
        String.format("%.2f", number)
}


@BindingAdapter("discountFormatted")
fun TextView.setDiscountFormatted(data: ReportItemsModel){
    if (data.discountAmount > 0) {
        text = String.format("Discount  ₹%.0f", data.discountAmount)
    } else {
        text = ""
    }
}


@BindingAdapter("addressFormatted")
fun TextView.setAddressFormatted(data: ReportItemsModel){
    var address = data.deliveryAddress
    text = address
}



@BindingAdapter("totalPriceFormatted")
fun TextView.setTotalPriceFormatted(data: ReportItemsModel){

    if(data.orderAmount <= 0) {
        text = ""
        return
    }

    var markupPrice = ""
    if (data.discountAmount > 0) {
        markupPrice = String.format("₹%.0f", data.orderAmount + data.discountAmount)
    }

    val receivableString = String.format("Receivable  %s ₹%.0f \n%s", markupPrice, data.orderAmount, data.paymentStatus)
    val receivaableStringFormatted = SpannableString(receivableString)
    receivaableStringFormatted.setSpan(StrikethroughSpan(),11,12+markupPrice.length,0)
    receivaableStringFormatted.setSpan(ForegroundColorSpan(Color.parseColor("#dbdbdb")),11,12+markupPrice.length,0)
    receivaableStringFormatted.setSpan(RelativeSizeSpan(0.6F), receivableString.length-data.paymentStatus.length, receivableString.length,0)

    text = receivaableStringFormatted
}



@BindingAdapter("aggregationFormatted")
fun TextView.setAggregationFormatted(list: List<ReportItemsModel>?){
    if (list != null) {
        var totalAmount = (0).toDouble()
        var totalQuantity = (0).toDouble()
        var uom = ""
        list.forEach { element ->
            totalAmount += (element.orderAmount)
            totalQuantity += element.displayQuantity
            uom = element.itemUom
        }
        text = String.format("₹%.0f  -  %s %s", totalAmount, getFormattedQtyNumber(totalQuantity), uom)
    }
}


@BindingAdapter("statusFormatted")
fun TextView.setStatusFormatted(data: ReportItemsModel){

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


private fun isOrderFreezed(data: ReportItemsModel) : Boolean {
    if (data.isFreezed.equals(true) &&
        (data.orderStatus.equals("ORDERED") ||
                data.orderStatus.isBlank())){
        return true
    }
    return false
}