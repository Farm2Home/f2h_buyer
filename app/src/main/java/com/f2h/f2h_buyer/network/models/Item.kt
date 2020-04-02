package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class Item (
    @Json(name = "item_id") val itemId: Long = -1,
    @Json(name = "group_id") val groupId: Long = -1,
    @Json(name = "farmer_user_id") val farmerUserId: Long = -1,
    @Json(name = "item_name") val groupName: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "uom") val uom: String = "",
    @Json(name = "price_per_unit") val pricePerUnit: Float = (-1).toFloat(),
    @Json(name = "item_availability") val itemAvailability: List<ItemAvailability>
)