package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class User (
    @Json(name = "user_id") val userId: Long? = -1,
    @Json(name = "user_name") val userName: String? = "",
    @Json(name = "address") val address: String? = "",
    @Json(name = "email") val email: String? = "",
    @Json(name = "mobile") val mobile: String? = "",
    @Json(name = "password") val password: String? = ""
)

