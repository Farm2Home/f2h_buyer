package com.f2h.f2h_buyer.screens.notification

data class NotificationItemsModel (
    var itemId: Long = 0,
    var title: String = "",
    var body: String = "",
    var isRead: Boolean = false

)