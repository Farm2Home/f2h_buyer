package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json


data class Order (
    @Json(name = "order_id") val orderId: Long? = -1L,
    @Json(name = "itemId") val itemId: Long? = -1L,
    @Json(name = "buyer_user_id") val buyerUserId: Long? = -1L,
    @Json(name = "seller_user_id") val sellerUserId: Long? = -1L,
    @Json(name = "item_availability_id") val itemAvailabilityId: Long? = -1L,
    @Json(name = "order_description") val orderDescription: String? = "",
    @Json(name = "ordered_quantity") val orderedQuantity: Double? = 0.0,
    @Json(name = "confirmed_quantity") val confirmedQuantity: Double? = 0.0,
    @Json(name = "ordered_amount") val orderedAmount: Double? = 0.0,
    @Json(name = "discount_amount") val discountAmount: Double? = 0.0,
    @Json(name = "order_status") val orderStatus: String? = "",
    @Json(name = "payment_status") val paymentStatus: String? = "",
    @Json(name = "ordered_date") val orderedDate: String? = "",
    @Json(name = "created_by") val createdBy: String? = "",
    @Json(name = "updated_by") val updatedBy: String? = "",
    @Json(name = "delivery_location") val deliveryLocation: String? = "",
    @Json(name = "order_comment") val orderComment: String? = "",
    @Json(name = "number_of_packets") val numberOfPackets: Int? = 1
)

data class ServiceOrder (
    @Json(name = "service_order_id") val serviceOrderId: Long? = -1L,
    @Json(name = "name") val name: String? = "",
    @Json(name = "description") val description: String? = "",
    @Json(name = "amount") val amount: Double? = 0.0,
    @Json(name = "created_by") val createdBy: String? = "",
    @Json(name = "updated_by") val updatedBy: String? = ""
)

data class OrderHeader (
    @Json(name = "order_header_id") val orderHeaderId: Long? = -1L,
    @Json(name = "packing_number") val packingNumber: Long? = -1L,
    @Json(name = "buyer_user_id") val buyerUserId: Long? = -1L,
    @Json(name = "group_id") val groupId: Long? = -1L,
    @Json(name = "delivery_slot_id") val deliverySlotId: Long? = -1L,
    @Json(name = "delivery_date") val deliveryDate: String? = "",
    @Json(name = "delivery_location") val deliveryLocation: String? = "",
    @Json(name = "total_farmer_amount") val totalFarmerAmount: Double? = 0.0,
    @Json(name = "total_v2_amount") val totalV2Amount: Double? = 0.0,
    @Json(name = "handling_amount") val handlingAmount: Double? = 0.0,
    @Json(name = "final_amount") val finalAmount: Double? = 0.0,
    @Json(name = "orders") val orders: List<Order> = listOf(),
    @Json(name = "service_orders") val serviceOrders: List<ServiceOrder> = listOf(),
    @Json(name = "created_by") val createdBy: String? = "",
    @Json(name = "updated_by") val updatedBy: String? = ""
)


data class OrderUpdateRequest (
    @Json(name = "order_id") var orderId: Long?,
    @Json(name = "order_status") var orderStatus: String?,
    @Json(name = "ordered_quantity") var orderedQuantity: Double?,
    @Json(name = "ordered_amount") var orderedAmount: Double?,
    @Json(name = "discount_amount") var discountAmount: Double?
)


data class OrderCreateRequest (
    @Json(name = "buyer_user_id") var buyerUserId: Long?,
    @Json(name = "item_availability_id") var itemAvailabilityId: Long?,
    @Json(name = "order_description") var orderDescription: String?,
    @Json(name = "delivery_location") var deliveryLocation: String?,
    @Json(name = "ordered_quantity") var orderedQuantity: Double?,
    @Json(name = "ordered_amount") var orderedAmount: Double?,
    @Json(name = "discount_amount") var discountAmount: Double?,
    @Json(name = "order_status") var orderStatus: String?,
    @Json(name = "payment_status") var paymentStatus: String?,
    @Json(name = "created_by") var createdBy: String?,
    @Json(name = "updated_by") var updatedBy: String?
)
