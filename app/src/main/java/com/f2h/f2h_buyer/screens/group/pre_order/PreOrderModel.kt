package com.f2h.f2h_buyer.screens.group.pre_order

data class PreOrderModel (
    var itemId: Long = 0,
    var itemAvailabilityId: Long = 0,
    var availableDate: String = "",
    var availableTimeSlot: String = "",
    var itemName: String = "",
    var itemDescription: String = "",
    var farmerName: String = "",
    var price: Float = 0F,
    var itemUom: String = "",
    var orderId: Long = 0,
    var orderedQuantity: Float = 0F,
    var orderUom: String = "",
    var orderAmount: Float = 0F,
    var discountAmount: Float = 0F
)
