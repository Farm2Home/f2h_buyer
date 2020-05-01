package com.f2h.f2h_buyer.screens.group.pre_order

data class PreOrderItemsModel (
    var itemAvailabilityId: Long = 0,
    var availableDate: String = "",
    var availableTimeSlot: String = "",
    var orderId: Long = 0,
    var orderedQuantity: Double = 0.0,
    var orderUom: String = ""
)
