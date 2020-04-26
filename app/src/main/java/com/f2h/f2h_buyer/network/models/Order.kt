package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json


data class Order (
    @Json(name = "order_id") val orderId: Long? = -1L,
    @Json(name = "group_id") val groupId: Long? = -1L,
    @Json(name = "seller_user_id") val sellerUserId: Long? = -1L,
    @Json(name = "buyer_user_id") val buyerUserId: Long? = -1L,
    @Json(name = "item_availability_id") val itemAvailabilityId: Long? = -1L,
    @Json(name = "order_description") val orderDescription: String? = "",
    @Json(name = "delivery_location") val deliveryLocation: String? = "",
    @Json(name = "uom") val uom: String? = "",
    @Json(name = "ordered_quantity") val orderedQuantity: Double? = 0.0,
    @Json(name = "confirmed_quantity") val confirmedQuantity: Double? = 0.0,
    @Json(name = "ordered_amount") val orderedAmount: Double? = 0.0,
    @Json(name = "discount_amount") val discountAmount: Double? = 0.0,
    @Json(name = "order_status") val orderStatus: String? = "",
    @Json(name = "payment_status") val paymentStatus: String? = "",
    @Json(name = "delivery_status") val deliveryStatus: String? = "",
    @Json(name = "delivery_comment") val deliveryComment: String? = "",
    @Json(name = "order_comment") val orderComment: String? = "",
    @Json(name = "delivered_date") val deliveredDate: String? = "",
    @Json(name = "delivery_time_slot")  val deliveryTimeSlot: String? = "",
    @Json(name = "ordered_date") val orderedDate: String? = "",
    @Json(name = "created_by") val createdBy: String? = "",
    @Json(name = "updated_by") val updatedBy: String? = ""
)


data class OrderUpdate (
    @Json(name = "order_id") var orderId: Long?,
    @Json(name = "ordered_quantity") var orderedQuantity: Double?,
    @Json(name = "ordered_amount") var orderedAmount: Double?,
    @Json(name = "discount_amount") var discountAmount: Double?
)
