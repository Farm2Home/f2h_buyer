
package com.f2h.f2h_buyer.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_CONFIRMED
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_DELIVERED
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_ORDERED
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_REJECTED
import com.f2h.f2h_buyer.screens.group.pre_order.PreOrderItemsModel
import com.f2h.f2h_buyer.screens.group.pre_order.PreOrderUiModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("descriptionFormatted")
fun TextView.setDescriptionFormatted(data: PreOrderUiModel?){
    data?.let {
        text = String.format("%s", data.itemDescription)
    }
}


@BindingAdapter("farmerFormatted")
fun TextView.setFarmerFormatted(data: PreOrderUiModel?){
    data?.let {
        if (data.farmerName.isNullOrBlank()){
            text = ""
            return
        }
        text = String.format("Farmer - %s (%s)", data.farmerName, data.farmerMobile)
    }
}


@BindingAdapter("toolbarTitleFormatted")
fun CollapsingToolbarLayout.setToolbarTitleFormattedFromPreOrderUiModel(data: PreOrderUiModel?){
    data?.let {
        title = String.format("%s (%s%.0f/%s)", data.itemName, data.currency, data.itemPrice, data.itemUom)
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
        // Default behaviour, show ordered quantity
        var orderedString = String.format("%s", getFormattedQtyNumber(data.orderedQuantity))
        if (data.orderStatus.equals(ORDER_STATUS_CONFIRMED) ||
            data.orderStatus.equals(ORDER_STATUS_DELIVERED)){
            orderedString = String.format("%s",getFormattedQtyNumber(data.confirmedQuantity))
        }
        text = orderedString
    }
}


@BindingAdapter("freezeTextFormatted")
fun TextView.setFreezeTextFormatted(data: PreOrderItemsModel?){
    var freezeText = ""
    data?.let {
        if (isFreezeStringDisplayed(data)){
            freezeText = "Freeze"
        }
    }
    val colouredText = SpannableString(freezeText)
    var color = ContextCompat.getColor(context, R.color.orange_status)
    colouredText.setSpan(ForegroundColorSpan(color),0, freezeText.length,0)
    text = colouredText
}


private fun isFreezeStringDisplayed(data: PreOrderItemsModel) =
    !isChangeQuantityButtonsEnabled(data) && (ORDER_STATUS_ORDERED.equals(data.orderStatus) || data.orderStatus.isBlank())


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

    if (displayedStatus.isBlank()){
        text = ""
        return
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
fun Button.setButtonVisibilityFormatted(data: PreOrderItemsModel){
    if(isChangeQuantityButtonsEnabled(data)){
        isEnabled = true
        visibility = View.VISIBLE
    } else {
        isEnabled = false
        visibility = View.INVISIBLE
    }
}


private fun getFormattedQtyNumber(number: Double): String {
    return if (number.compareTo(number.toLong()) == 0)
        String.format("%d", number.toLong())
    else
        String.format("%.2f", number)
}

private fun isChangeQuantityButtonsEnabled(data: PreOrderItemsModel) : Boolean {
    if (data.isFreezed.equals(false) &&
        (data.orderStatus.equals(ORDER_STATUS_ORDERED) ||
                data.orderStatus.isBlank())){
        return true
    }
    return false
}