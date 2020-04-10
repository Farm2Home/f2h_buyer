package com.f2h.f2h_buyer.screens.group.group_wallet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GroupWalletViewModelFactory (
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupWalletViewModel::class.java)) {
            return GroupWalletViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}