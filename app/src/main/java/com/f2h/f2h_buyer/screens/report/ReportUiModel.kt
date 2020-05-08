package com.f2h.f2h_buyer.screens.report

data class ReportUiModel (
    var selectedItem: String = "",
    var selectedOrderStatus: String = "",
    var selectedPaymentStatus: String = "",
    var itemList: List<String> = arrayListOf(),
    var orderStatusList: List<String> = arrayListOf(),
    var paymentStatusList: List<String> = arrayListOf()
)
