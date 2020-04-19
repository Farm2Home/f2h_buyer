package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class ItemAvailability (
    @Json(name = "item_id") val itemId: Long = -1,
    @Json(name = "item_availability_id") val itemAvailabilityId: Long = -1L,
    @Json(name = "available_date") val availableDate: String = "",
    @Json(name = "available_time_slot") val availableTimeSlot: String = "",
    @Json(name = "stock_quantity") val stockQuantity: Float = 0F,
    @Json(name = "committed_quantity") val committedQuantity: Float = 0F,
    @Json(name = "available_quantity") val availableQuantity: Float = 0F,
    @Json(name = "is_freezed") val isFreezed: Boolean = false
)


//data class ItemAvailability (
//    var itemId: Long = -1L,
//    var itemAvailabilityId: Long = -1L,
//    var availableDate: String = "",
//    var availableTimeSlot: String = "",
//    var stockQuantity: Float = 0F,
//    var committedQuantity: Float = 0F,
//    var availableQuantity: Float = 0F,
//    var isFreezed: Boolean = false
//)
//
//
//class ItemAvailabilityAdapter {
//    @FromJson
//    fun itemAvailabilityFromJson(itemAvailabilityJson: ItemAvailabilityJson): ItemAvailability {
//        val itemAvailability = ItemAvailability()
//        itemAvailability.itemId = itemAvailabilityJson.itemId ?: -1L
//        itemAvailability.itemAvailabilityId = itemAvailabilityJson.itemAvailabilityId ?: -1L
//        itemAvailability.availableDate = itemAvailabilityJson.availableDate ?: ""
//        itemAvailability.availableTimeSlot = itemAvailabilityJson.availableTimeSlot ?: ""
//        itemAvailability.stockQuantity = itemAvailabilityJson.stockQuantity ?: 0F
//        itemAvailability.committedQuantity = itemAvailabilityJson.committedQuantity ?: 0F
//        itemAvailability.availableQuantity = itemAvailabilityJson.availableQuantity ?: 0F
//        itemAvailability.isFreezed = itemAvailabilityJson.isFreezed ?: false
//        return itemAvailability
//    }
//}


