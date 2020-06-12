package com.f2h.f2h_buyer.constants

import com.f2h.f2h_buyer.BuildConfig
import com.f2h.f2h_buyer.constants.F2HConstants.SERVER_URL

object F2HConstants {

    /***********************************************************
     * Use the below 2 URLs for testing and produciton purposes
     ***********************************************************/
    const val SERVER_URL = BuildConfig.SERVER_URL

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