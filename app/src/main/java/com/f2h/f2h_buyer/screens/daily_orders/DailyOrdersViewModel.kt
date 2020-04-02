package com.f2h.f2h_buyer.screens.daily_orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.ItemApi
import kotlinx.coroutines.*

class DailyOrdersViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _items = MutableLiveData<String>()
    val items: LiveData<String>
        get() = _items


    private val _sessionData = MutableLiveData<SessionEntity>()
    val sessionData: LiveData<SessionEntity>
        get() = _sessionData


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getItemsAndAvailabilitiesForGroup()
    }

    fun refreshFragmentData(){
        getItemsAndAvailabilitiesForGroup()
    }

    private fun getItemsAndAvailabilitiesForGroup() {
        coroutineScope.launch {
            _sessionData.value = retrieveSession()
            var getItemsDataDeferred = ItemApi.retrofitService.getItemsForGroup(_sessionData.value!!.groupId)
            try {
                var items = getItemsDataDeferred.await()
                println("Item API response : " + items.toString())
                if (items != null && items.size > 0) {
                    _items.value = items.toString()
                }
            } catch (t:Throwable){
                println(t.message)
            }
        }
    }


    private suspend fun retrieveSession() : SessionEntity {
        return withContext(Dispatchers.IO) {
            val sessions = database.getAll()
            var session = SessionEntity()
            if (sessions != null && sessions.size==1) {
                session = sessions[0]
                println(session.toString())
            } else {
                database.clearSessions()
            }
            return@withContext session
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}