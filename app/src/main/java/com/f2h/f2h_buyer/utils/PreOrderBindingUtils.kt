package com.f2h.f2h_buyer.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.screens.group.daily_orders.DailyOrdersUiModel
import com.f2h.f2h_buyer.screens.group.pre_order.PreOrderItemsModel
import com.f2h.f2h_buyer.screens.group.pre_order.PreOrderUiModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("namePriceFormattedPreOrder")
fun TextView.setNamePriceFormattedFromPreOrderUiModel(data: PreOrderUiModel?){
    data?.let {
        text = String.format("%s (₹%.0f/%s)", data.itemName, data.itemPrice, data.itemUom)
    }
}

@BindingAdapter("toolbarTitleFormatted")
fun CollapsingToolbarLayout.setToolbarTitleFormattedFromPreOrderUiModel(data: PreOrderUiModel?){
    data?.let {
        title = String.format("%s (₹%.0f/%s)", data.itemName, data.itemPrice, data.itemUom)
        setExpandedTitleColor(Color.WHITE)
    }
}

@BindingAdapter("dateFormattedPreOrderItems")
fun TextView.setDateFormattedPreOrderItems(data: PreOrderItemsModel?){
    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    val df_out: DateFormat = SimpleDateFormat("dd-MMM\nEEEE")
    data?.let {
        var date: String = df_out.format(df.parse(data.availableDate))
        text = String.format("%s", date)
    }
}


@BindingAdapter("orderedQuantityFormattedPreOrder")
fun TextView.setOrderedQuantityFormattedPreOrder(data: PreOrderItemsModel?){
    data?.let {
        var freezeString = ""
        if (isOrderFreezed(data)){
            freezeString = "\nFreeze"
        }
        var orderedString = String.format("%s %s", getFormattedQtyNumber(data.orderedQuantity), freezeString)
        if (data.orderStatus.equals("CONFIRMED")){
            orderedString = String.format("%s %s",getFormattedQtyNumber(data.confirmedQuantity), freezeString)
        }
        text = orderedString
    }
}

@BindingAdapter("availableQuantityFormattedPreOrder")
fun TextView.setAvailableQuantityFormattedPreOrder(data: PreOrderItemsModel?){
    data?.let {
        var availabileQuantityText = getFormattedQtyNumber(data.availableQuantity - data.quantityChange)
        var itemUom = data.itemUom
        if(data.availableQuantity - data.quantityChange > 1000){
            availabileQuantityText = "Unlimited"
            itemUom = ""
        }

        var orderedString = String.format("%s  %s", availabileQuantityText, itemUom)
        text = orderedString
    }
}


@BindingAdapter("statusFormatted")
fun TextView.setStatusFormatted(data: PreOrderItemsModel){

    var displayedStatus: String = data.orderStatus

    if (data.deliveryStatus.equals("DELIVERY_STARTED") ||
        data.deliveryStatus.equals("DELIVERED")){
        displayedStatus = data.deliveryStatus
    }

    if (displayedStatus.isBlank()){
        text = ""
        return
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
fun Button.setButtonVisibilityFormatted(data: PreOrderItemsModel){
    if (isOrderFreezed(data)){
        isEnabled = false
    } else{
        isEnabled = true
    }
}


private fun getFormattedQtyNumber(number: Double): String {
    return if (number.compareTo(number.toLong()) == 0)
        String.format("%d", number.toLong())
    else
        String.format("%.2f", number)
}

private fun isOrderFreezed(data: PreOrderItemsModel) : Boolean {
    if (data.isFreezed.equals(true) &&
        (data.orderStatus.equals("ORDERED") ||
                data.orderStatus.isBlank())){
        return true
    }
    return false
}