package com.f2h.f2h_buyer.screens.group.daily_orders

import com.f2h.f2h_buyer.network.models.Comment

data class DailyOrdersUiModel (
    var itemId: Long = 0,
    var orderedDate: String = "",
    var itemName: String = "",
    var itemDescription: String = "",
    var farmerName: String = "",
    var farmerMobile: String = "",
    var price: Double = 0.0,
    var itemUom: String = "",
    var itemImageLink: String = "",
    var orderId: Long = 0,
    var quantityChange: Double = 0.0,
    var orderedQuantity: Double = 0.0,
    var confirmedQuantity: Double = 0.0,
    var availableQuantity: Double = 0.0,
    var orderQtyJump: Double = 0.0,
    var orderAmount: Double = 0.0,
    var orderStatus: String = "",
    var paymentStatus: String = "",
    var isFreezed: Boolean = false,
    var discountAmount: Double = 0.0,
    var isMoreDetailsDisplayed: Boolean = false,
    var comments: ArrayList<Comment> = arrayListOf(),
    var newComment: String = "",
    var isCommentProgressBarActive: Boolean = false
)
