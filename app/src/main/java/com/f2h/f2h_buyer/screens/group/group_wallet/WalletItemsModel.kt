package com.f2h.f2h_buyer.screens.group.group_wallet

data class WalletItemsModel (
    var walletLedgerId: Long = 0,
    var transactionDate: String = "",
    var transactionDescription: String = "",
    var currency: String = "",
    var amount: Double = 0.0
)