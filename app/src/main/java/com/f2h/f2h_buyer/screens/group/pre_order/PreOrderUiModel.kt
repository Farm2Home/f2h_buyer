package com.f2h.f2h_buyer.screens.group.pre_order


data class PreOrderUiModel (
    var itemId: Long = -1,
    var itemName: String = "",
    var itemDescription: String = "",
    var itemUom: String = "",
    var itemImageLink: String = "",
    var currency: String = "",
    var itemPrice: Double = 0.0,
    var farmerName: String = "",
    var farmerMobile: String = ""
)
