package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.Json

data class GroupMembershipRequest (
    @Json(name = "group_id") val groupId: Long? = -1L,
    @Json(name = "user_id") val userId: Long? = -1L,
    @Json(name = "roles") val roles: String? = "",
    @Json(name = "created_by") val createdBy: String? = ""
)


data class GroupMembership (
    @Json(name = "group_id") val groupId: Long? = -1L,
    @Json(name = "user_id") val userId: Long? = -1L,
    @Json(name = "roles") val roles: String? = "",
    @Json(name = "created_by") val createdBy: String? = "",
    @Json(name = "updated_by") val updatedBy: String? = ""
)
