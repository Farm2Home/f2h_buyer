package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class Group (
    @Json(name = "group_id") val groupId: Long = -1,
    @Json(name = "owner_user_id") val ownerUserId: Long = -1,
    @Json(name = "group_name") val groupName: String = "",
    @Json(name = "description") val description: String = ""
)