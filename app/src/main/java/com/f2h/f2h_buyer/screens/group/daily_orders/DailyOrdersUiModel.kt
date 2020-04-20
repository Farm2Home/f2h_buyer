package com.f2h.f2h_buyer.screens.group.daily_orders

data class DailyOrdersUiModel (
    var itemId: Long = 0,
    var orderedDate: String = "",
    var itemName: String = "",
    var itemDescription: String = "",
    var farmerName: String = "",
    var price: Float = 0F,
    var itemUom: String = "",
    var orderId: Long = 0,
    var orderedQuantity: Float = 0F,
    var availableQuantity: Float = 0F,
    var orderUom: String = "",
    var orderAmount: Float = 0F,
    var orderStatus: String= "",
    var paymentStatus: String = "",
    var deliveryStatus: String = "",
    var isFreezed: Boolean = false,
    var discountAmount: Float = 0F
)
