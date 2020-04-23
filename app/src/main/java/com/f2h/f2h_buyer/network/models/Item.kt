package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class Item (
    @Json(name = "item_id") var itemId: Long? = -1,
    @Json(name = "group_id") var groupId: Long? = -1,
    @Json(name = "farmer_user_id") var farmerUserId: Long? = -1,
    @Json(name = "farmer_user_name") var farmerUserName: String? = "",
    @Json(name = "item_name") var itemName: String? = "",
    @Json(name = "description") var description: String? = "",
    @Json(name = "uom") var uom: String? = "",
    @Json(name = "price_per_unit") var pricePerUnit: Float? = 0F,
    @Json(name = "confirm_qty_jump") var confirmQtyJump: Float? = 0F,
    @Json(name = "order_qty_jump") var orderQtyJump: Float? = 0F,
    @Json(name = "item_availability") var itemAvailability: List<ItemAvailability> = listOf()
)
