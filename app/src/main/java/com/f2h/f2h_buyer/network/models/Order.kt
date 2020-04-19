package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json


data class OrderJson (
    @Json(name = "order_id") val orderId: Long? = -1L,
    @Json(name = "group_id") val groupId: Long? = -1L,
    @Json(name = "seller_user_id") val sellerUserId: Long? = -1L,
    @Json(name = "buyer_user_id") val buyerUserId: Long? = -1L,
    @Json(name = "item_availability_id") val itemAvailabilityId: Long? = -1L,
    @Json(name = "order_description") val orderDescription: String? = "",
    @Json(name = "delivery_location") val deliveryLocation: String? = "",
    @Json(name = "uom") val uom: String? = "",
    @Json(name = "ordered_quantity") val orderedQuantity: Float? = 0F,
    @Json(name = "confirmed_quantity") val confirmedQuantity: Float? = 0F,
    @Json(name = "ordered_amount") val orderedAmount: Float? = 0F,
    @Json(name = "discount_amount") val discountAmount: Float? = 0F,
    @Json(name = "order_status") val orderStatus: String? = "",
    @Json(name = "payment_status") val paymentStatus: String? = "",
    @Json(name = "delivery_status") val deliveryStatus: String? = "",
    @Json(name = "delivery_comment") val deliveryComment: String? = "",
    @Json(name = "delivered_date") val deliveredDate: String? = "",
    @Json(name = "delivery_time_slot")  val deliveryTimeSlot: String? = "",
    @Json(name = "ordered_date") val orderedDate: String? = "",
    @Json(name = "created_by") val createdBy: String? = "",
    @Json(name = "updated_by") val updatedBy: String? = ""
)


data class Order (
    var orderId: Long? = -1L,
    var groupId: Long? = -1L,
    var sellerUserId: Long? = -1L,
    var buyerUserId: Long? = -1L,
    var itemAvailabilityId: Long? = -1L,
    var orderDescription: String? = "",
    var deliveryLocation: String? = "",
    var uom: String? = "",
    var orderedQuantity: Float? = 0F,
    var confirmedQuantity: Float? = 0F,
    var orderedAmount: Float? = 0F,
    var discountAmount: Float? = 0F,
    var orderStatus: String? = "",
    var paymentStatus: String? = "",
    var deliveryStatus: String? = "",
    var deliveryComment: String? = "",
    var deliveredDate: String? = "",
    var deliveryTimeSlot: String? = "",
    var orderedDate: String? = "",
    var createdBy: String? = "",
    var updatedBy: String? = ""
)

class OrderAdapter {
    @FromJson
    fun orderFromJson(orderJson: OrderJson): Order {
        val order = Order()
        order.orderId = orderJson.orderId ?: -1L
        order.groupId = orderJson.groupId ?: -1L
        order.sellerUserId = orderJson.sellerUserId ?: -1L
        order.buyerUserId = orderJson.buyerUserId ?: -1L
        order.itemAvailabilityId = orderJson.itemAvailabilityId ?: -1L
        order.orderDescription = orderJson.orderDescription ?: ""
        order.deliveryLocation = orderJson.deliveryLocation ?: ""
        order.uom = orderJson.uom ?: ""
        order.orderedQuantity = orderJson.orderedQuantity ?: 0F
        order.confirmedQuantity = orderJson.confirmedQuantity ?: 0F
        order.orderedAmount = orderJson.orderedAmount ?: 0F
        order.discountAmount = orderJson.discountAmount ?: 0F
        order.orderStatus = orderJson.orderStatus ?: ""
        order.paymentStatus = orderJson.paymentStatus ?: ""
        order.deliveryStatus = orderJson.deliveryStatus ?: ""
        order.deliveryComment = orderJson.deliveryComment ?: ""
        order.deliveredDate = orderJson.deliveredDate ?: ""
        order.deliveryTimeSlot = orderJson.deliveryTimeSlot ?: ""
        order.orderedDate = orderJson.orderedDate ?: ""
        order.createdBy = orderJson.createdBy ?: ""
        order.updatedBy = orderJson.updatedBy ?: ""
        return order
    }
}
