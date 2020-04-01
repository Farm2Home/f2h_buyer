package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class Item (
    @Json(name = "item_id") val itemId: Long,
    @Json(name = "group_id") val groupId: Long,
    @Json(name = "farmer_user_id") val farmerUserId: Long,
    @Json(name = "item_name") val groupName: String,
    @Json(name = "description") val description: String,
    @Json(name = "uom") val uom: String,
    @Json(name = "price_per_unit") val pricePerUnit: Float
)