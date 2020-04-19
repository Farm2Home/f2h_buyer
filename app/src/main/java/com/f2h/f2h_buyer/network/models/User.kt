package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json

data class UserJson (
    @Json(name = "user_id") val userId: Long? = -1,
    @Json(name = "user_name") val userName: String? = "",
    @Json(name = "address") val address: String? = "",
    @Json(name = "email") val email: String? = "",
    @Json(name = "mobile") val mobile: String? = "",
    @Json(name = "password") val password: String? = ""
)


data class User (
    var userId: Long = -1,
    var userName: String = "",
    var address: String = "",
    var email: String = "",
    var mobile: String = "",
    var password: String = ""
)


class UserAdapter {
    @FromJson
    fun groupFromJson(userJson: UserJson): User {
        val user = User()
        user.userId = userJson.userId ?: -1L
        user.userName = userJson.userName ?: ""
        user.address = userJson.address ?: ""
        user.email = userJson.email ?: ""
        user.mobile = userJson.mobile ?: ""
        user.password = userJson.password ?: ""
        return user
    }
}

