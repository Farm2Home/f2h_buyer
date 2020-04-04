package com.f2h.f2h_buyer.screens.daily_orders

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.ItemApi
import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.network.models.ItemAvailability
import kotlinx.coroutines.*
import java.time.Instant
import java.time.temporal.ChronoUnit

class DailyOrdersViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _visibleItems = MutableLiveData<List<Item>>()
    val visibleItems: LiveData<List<Item>>
        get() = _visibleItems

    private val _sessionData = MutableLiveData<SessionEntity>()
    val sessionData: LiveData<SessionEntity>
        get() = _sessionData

    private var selectedDate = Instant.now()
    private var selectedTimeSlot = "Morning"

    private val _allItems = MutableLiveData<List<Item>>()
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
                if (items.size > 0) {
                    _allItems.value = items
                    filterVisibleItems(items)
                }
            } catch (t:Throwable){
                println(t.message)
            }
        }
    }

    private fun filterVisibleItems(items: List<Item>) {
        var filteredItems = ArrayList<Item>()
        items.forEach {item ->
            item.itemAvailability.forEach { itemAvailability ->
                if (itemAvailability.availableTimeSlot.equals(selectedTimeSlot) &&
                    isDateEqual(itemAvailability.availableDate, selectedDate)){
                    filteredItems.add(item)
                }
            }
        }
        _visibleItems.value = filteredItems
    }

    private fun isDateEqual(availableDate: String, selectedDate: Instant?): Boolean {
        var date1: Long = Instant.parse(availableDate).truncatedTo(ChronoUnit.DAYS).toEpochMilli()
        var date2: Long = selectedDate?.truncatedTo(ChronoUnit.DAYS)?.toEpochMilli() ?: 0
        return (date1 == date2)
    }


    private suspend fun retrieveSession() : SessionEntity {
        return withContext(Dispatchers.IO) {
            val sessions = database.getAll()
            var session = SessionEntity()
            if (sessions.size==1) {
                session = sessions[0]
                println(session.toString())
            } else {
                database.clearSessions()
            }
            return@withContext session
        }
    }


    fun updateSelectedDate(date: Instant){
        selectedDate = date
        _allItems.value?.let { filterVisibleItems(it) }
    }

    fun updateSelectedTimeSlot(timeSlot: String){
        selectedTimeSlot = timeSlot
        _allItems.value?.let { filterVisibleItems(it) }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}