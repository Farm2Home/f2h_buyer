package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class Group (
    @Json(name = "group_id") val groupId: Long,
    @Json(name = "group_name") val groupName: String,
    @Json(name = "description") val description: String
)