package com.f2h.f2h_buyer.screens.group.all_items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.ItemApi
import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.network.models.ItemAvailability
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class AllItemsViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

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
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        items.forEach {item ->
            var nextItemAvailability = item.itemAvailability.get(0)
            item.itemAvailability.forEach { itemAvailability ->
                val itemAvailabilityDate = parser.parse(itemAvailability.availableDate)
                val nextItemAvailabilityDate = parser.parse(item.itemAvailability.get(0).availableDate)
                if (Calendar.getInstance().time <= itemAvailabilityDate &&
                    itemAvailabilityDate < nextItemAvailabilityDate){
                    nextItemAvailability = itemAvailability
                }
            }
            item.itemAvailability = arrayOf(nextItemAvailability).toList()
            filteredItems.add(item)
        }
        _visibleItems.value = filteredItems
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

    fun updateSelectedTimeSlot(timeSlot: String){
        selectedTimeSlot = timeSlot
        _allItems.value?.let { filterVisibleItems(it) }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}