package com.f2h.f2h_buyer.screens.group.daily_orders

data class DailyOrdersUiModel (
    var itemId: Long = 0,
    var orderedDate: String = "",
    var itemName: String = "",
    var itemDescription: String = "",
    var farmerName: String = "",
    var price: Float = (0).toFloat(),
    var itemUom: String = "",
    var orderId: Long = 0,
    var orderedQuantity: Float = (0).toFloat(),
    var orderUom: String = "",
    var orderAmount: Float = (0).toFloat(),
    var orderStatus: String= "",
    var paymentStatus: String = "",
    var deliveryStatus: String = "",
    var isFreezed: Boolean = false,
    var discountAmount: Float = (0).toFloat()
)
