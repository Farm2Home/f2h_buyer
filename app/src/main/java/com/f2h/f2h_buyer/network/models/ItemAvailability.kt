package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class ItemAvailability (
    @Json(name = "item_id") val itemId: Long = -1,
    @Json(name = "item_availability_id") val itemAvailabilityId: Long = -1,
    @Json(name = "available_date") val availableDate: String = "",
    @Json(name = "available_time_slot") val availableTimeSlot: String = "",
    @Json(name = "stock_quantity") val stockQuantity: Float = (-1).toFloat(),
    @Json(name = "committed_quantity") val committedQuantity: Float = (-1).toFloat(),
    @Json(name = "available_quantity") val availableQuantity: Float = (-1).toFloat(),
    @Json(name = "is_freezed") val isFreezed: Boolean = false
)