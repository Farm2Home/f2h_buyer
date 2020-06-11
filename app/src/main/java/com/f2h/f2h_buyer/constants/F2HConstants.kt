package com.f2h.f2h_buyer.constants

object F2HConstants {
    const val ORDER_STATUS_ORDERED = "ORDERED"
    const val ORDER_STATUS_CONFIRMED = "CONFIRMED"
    const val ORDER_STATUS_REJECTED = "REJECTED"
    const val ORDER_STATUS_DELIVERED = "DELIVERED"

    const val PAYMENT_STATUS_PENDING = "PENDING"
    const val PAYMENT_STATUS_PAID = "PAID"

    const val USER_ROLE_BUYER = "BUYER"
    const val USER_ROLE_BUYER_REQUESTED = "BUYER_REQUESTED"
    const val USER_ROLE_GROUP_ADMIN = "GROUP_ADMIN"
}