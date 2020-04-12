package com.f2h.f2h_buyer.screens.group.daily_orders

import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.network.models.Order

data class DailyOrdersModel (
    var item: Item = Item(),
    var order: Order = Order()
)
