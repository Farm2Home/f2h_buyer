package com.f2h.f2h_buyer.network.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json

data class GroupJson (
    @Json(name = "group_id") val groupId: Long? = -1L,
    @Json(name = "owner_user_id") val ownerUserId: Long? = -1L,
    @Json(name = "group_name") val groupName: String? = "",
    @Json(name = "description") val description: String? = ""
)


data class Group (
    var groupId: Long = -1L,
    var ownerUserId: Long = -1L,
    var groupName: String = "",
    var description: String = ""
)


class GroupAdapter {
    @FromJson
    fun groupFromJson(groupJson: GroupJson): Group {
        val group = Group()
        group.groupId = groupJson.groupId ?: -1L
        group.ownerUserId = groupJson.groupId ?: -1L
        group.groupName = groupJson.groupName ?: ""
        group.description = groupJson.description ?: ""
        return group
    }
}
