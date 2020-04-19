package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class Item (
    @Json(name = "item_id") var itemId: Long = -1,
    @Json(name = "group_id") var groupId: Long = -1,
    @Json(name = "farmer_user_id") var farmerUserId: Long = -1,
    @Json(name = "farmer_user_name") var farmerUserName: String = "",
    @Json(name = "item_name") var itemName: String = "",
    @Json(name = "description") var description: String = "",
    @Json(name = "uom") var uom: String = "",
    @Json(name = "price_per_unit") var pricePerUnit: Float = 0F,
    @Json(name = "item_availability") var itemAvailability: List<ItemAvailability> = listOf()
)

//data class Item (
//    var itemId: Long = -1,
//    var groupId: Long = -1,
//    var farmerUserId: Long = -1,
//    var farmerUserName: String = "",
//    var itemName: String = "",
//    var description: String = "",
//    var uom: String = "",
//    var pricePerUnit: Float = 0F,
//    var itemAvailability: ArrayList<ItemAvailability> = arrayListOf()
//)
//
//
//class ItemAdapter {
//    @FromJson
//    fun itemFromJson(itemJson: ItemJson): Item {
//        val item = Item()
//        item.itemId = itemJson.itemId ?: -1L
//        item.groupId = itemJson.groupId ?: -1L
//        item.farmerUserId = itemJson.farmerUserId ?: -1L
//        item.farmerUserName = itemJson.farmerUserName ?: ""
//        item.itemName = itemJson.itemName ?: ""
//        item.description = itemJson.description ?: ""
//        item.uom = itemJson.uom ?: ""
//        item.pricePerUnit = itemJson.pricePerUnit ?: 0F
//
//        itemJson.itemAvailability?.forEach { availability ->
//            item.itemAvailability.add(ItemAvailabilityAdapter().itemAvailabilityFromJson(availability))
//        }
//
//        return item
//    }
//}