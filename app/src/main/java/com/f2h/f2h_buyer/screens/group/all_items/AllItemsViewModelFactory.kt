package com.f2h.f2h_buyer.screens.group.daily_orders

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.screens.group.all_items.AllItemsViewModel

class AllItemsViewModelFactory (
    private val dataSource: SessionDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllItemsViewModel::class.java)) {
            return AllItemsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}