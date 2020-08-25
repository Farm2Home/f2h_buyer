package com.f2h.f2h_buyer.screens.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.NotificationDatabaseDao
import com.f2h.f2h_buyer.database.NotificationEntity
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat


class NotificationViewModel(val database: NotificationDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private var _visibleUiData = MutableLiveData<MutableList<NotificationEntity>>()
    val visibleUiData: LiveData<MutableList<NotificationEntity>>
        get() = _visibleUiData

    private var _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _isProgressBarActive.value = true
        getNotification()
    }


    fun getNotification() {
        _isProgressBarActive.value = true
        coroutineScope.launch {
            var notifications = retrieveNotification()
            _visibleUiData.value = retrieveNotification().toMutableList()
            updateNotificationAsRead(notifications.filter { !it.isRead })
        }
        _isProgressBarActive.value = false
    }


    private suspend fun retrieveNotification() : List<NotificationEntity> {
        return withContext(Dispatchers.IO) {
            val notifications = database.getAll()
            return@withContext notifications
        }
    }

    private suspend fun updateNotificationAsRead(notificationEntities: List<NotificationEntity> ) {
        withContext(Dispatchers.IO) {
            notificationEntities.forEach {notificationEntity->
                notificationEntity.isRead = true
                database.update(notificationEntity)
            }
        }
    }

    fun clearAll(){
        _isProgressBarActive.value = true
        coroutineScope.launch {
            clearAllNotification()
            _visibleUiData.value = ArrayList()
        }
        _isProgressBarActive.value = false
    }
    private suspend fun clearAllNotification() {
        withContext(Dispatchers.IO) {
            database.removeAll()
        }
    }



    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}