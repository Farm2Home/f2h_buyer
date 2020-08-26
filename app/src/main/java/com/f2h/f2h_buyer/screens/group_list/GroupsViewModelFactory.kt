package com.f2h.f2h_buyer.screens.group_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.f2h.f2h_buyer.database.NotificationDatabaseDao
import com.f2h.f2h_buyer.database.SessionDatabaseDao

class GroupsViewModelFactory (
    private val dataSource: SessionDatabaseDao,
    private val notificationDataSource: NotificationDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupsViewModel::class.java)) {
            return GroupsViewModel(dataSource, notificationDataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}