package com.f2h.f2h_buyer.screens.report

data class ReportItemsModel (
    var itemId: Long = 0,
    var orderedDate: String = "",
    var itemName: String = "",
    var itemDescription: String = "",
    var buyerName: String = "",
    var price: Double = 0.0,
    var itemUom: String = "",
    var orderId: Long = 0,
    var orderedQuantity: Double = 0.0,
    var confirmedQuantity: Double = 0.0,
    var availableQuantity: Double = 0.0,
    var orderAmount: Double = 0.0,
    var orderStatus: String = "",
    var orderComment: String = "",
    var paymentStatus: String = "",
    var deliveryStatus: String = "",
    var deliveryAddress: String = "",
    var isFreezed: Boolean = false,
    var discountAmount: Double = 0.0
)
