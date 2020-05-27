package com.f2h.f2h_buyer.screens.report

data class ReportUiModel (
    var selectedItem: String = "",
    var selectedDisplayStatus: String = "",
    var selectedPaymentStatus: String = "",
    var selectedStartDate: String = "",
    var selectedEndDate: String = "",
    var selectedBuyer: String = "",
    var itemList: List<String> = arrayListOf(),
    var displayStatusList: List<String> = arrayListOf(),
    var paymentStatusList: List<String> = arrayListOf(),
    var startDateList: List<String> = arrayListOf(),
    var endDateList: List<String> = arrayListOf(),
    var buyerNameList: List<String> = arrayListOf()
)
