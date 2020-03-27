package com.f2h.f2h_buyer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_table")
data class SessionEntity (

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "user_id")
    var userId: Long = 0L,

    @ColumnInfo(name = "active_group_id")
    var activeGroupId: Long = 0L,

    @ColumnInfo(name = "user_name")
    var userName: String = "",

    @ColumnInfo(name = "address")
    var address: String = "",

    @ColumnInfo(name = "email")
    var email: String = "",

    @ColumnInfo(name = "mobile")
    var mobile: String = "",

    @ColumnInfo(name = "password")
    var password: String = ""

)