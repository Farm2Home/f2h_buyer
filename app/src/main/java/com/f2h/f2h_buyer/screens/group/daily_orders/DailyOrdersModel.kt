package com.f2h.f2h_buyer.screens.group.daily_orders

data class DailyOrdersModel (
    var itemId: Long = 0,
    var itemAvailabilityId: Long = 0,
    var availableDate: String = "",
    var availableTimeSlot: String = "",
    var itemName: String = "",
    var itemDescription: String = "",
    var farmerName: String = "",
    var price: Float = (0).toFloat(),
    var itemUom: String = "",
    var orderId: Long = 0,
    var orderedQuantity: Float = (0).toFloat(),
    var orderUom: String = "",
    var orderAmount: Float = (0).toFloat(),
    var discountAmount: Float = (0).toFloat()
)
