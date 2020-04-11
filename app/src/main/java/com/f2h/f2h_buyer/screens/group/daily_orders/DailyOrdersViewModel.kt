package com.f2h.f2h_buyer.screens.group.daily_orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.ItemApi
import com.f2h.f2h_buyer.network.models.Item
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DailyOrdersViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private val _visibleItems = MutableLiveData<List<Item>>()
    val visibleItems: LiveData<List<Item>>
        get() = _visibleItems

    private val _sessionData = MutableLiveData<SessionEntity>()
    val sessionData: LiveData<SessionEntity>
        get() = _sessionData

    private var selectedDate = Calendar.getInstance().time
    private var selectedTimeSlot = "Morning"

    private val _allItems = MutableLiveData<List<Item>>()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _isProgressBarActive.value = true
        getItemsAndAvailabilitiesForGroup()
    }


    fun refreshFragmentData(){
        _isProgressBarActive.value = true
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
            _isProgressBarActive.value = false
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

    private fun isDateEqual(itemDate: String, selectedDate: Date): Boolean {

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedItemAvailableDate = df.parse(itemDate)
        val formattedSelectedDate = df.parse(df.format(selectedDate))
        return (formattedItemAvailableDate.time.equals(formattedSelectedDate.time))
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


    fun updateSelectedDate(date: Date){
        selectedDate = date
        _allItems.value?.let { filterVisibleItems(it) }
    }

//    fun updateSelectedTimeSlot(timeSlot: String){
//        selectedTimeSlot = timeSlot
//        _allItems.value?.let { filterVisibleItems(it) }
//    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}