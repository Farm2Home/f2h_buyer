package com.f2h.f2h_buyer.screens.group.daily_orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.ItemApi
import com.f2h.f2h_buyer.network.OrderApi
import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.network.models.Order
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DailyOrdersViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private var _visibleUiData = MutableLiveData<MutableList<DailyOrdersModel>>()
    val visibleUiData: LiveData<MutableList<DailyOrdersModel>>
        get() = _visibleUiData

    private val sessionData = MutableLiveData<SessionEntity>()
    private var selectedDate = Calendar.getInstance().time
    private var selectedTimeSlot = "Morning"

    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var allUiData = ArrayList<DailyOrdersModel>()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _isProgressBarActive.value = true
        getItemsAndAvailabilitiesForGroup()
    }


    private fun getItemsAndAvailabilitiesForGroup() {
        coroutineScope.launch {
            sessionData.value = retrieveSession()
            var getItemsDataDeferred = ItemApi.retrofitService.getItemsForGroup(sessionData.value!!.groupId)
            var getOrdersDataDeferred = OrderApi.retrofitService.getOrdersForGroupAndUser(sessionData.value!!.groupId, sessionData.value!!.userId)
            try {
                var items = getItemsDataDeferred.await()
                var orders = getOrdersDataDeferred.await()
                allUiData = createAllUiData(items, orders)
                if (allUiData.size > 0) {
                    _visibleUiData.value = filterVisibleItems(allUiData)
                }
            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }




    private fun createAllUiData(items: List<Item>, orders: List<Order>): ArrayList<DailyOrdersModel> {
        var allUiData = ArrayList<DailyOrdersModel>()
        items.forEach { item ->
            item.itemAvailability.forEach { availability ->
                var uiElement = DailyOrdersModel()
                uiElement.itemId = item.itemId
                uiElement.itemName = item.itemName
                uiElement.itemDescription = item.description
                uiElement.itemUom = item.uom
                uiElement.farmerName = item.farmerUserName
                uiElement.availableDate = df.format(df.parse(availability.availableDate) ?: "")
                uiElement.availableTimeSlot = availability.availableTimeSlot
                uiElement.itemAvailabilityId = availability.itemAvailabilityId
                uiElement.price = item.pricePerUnit
                uiElement.isFreezed = availability.isFreezed

                orders.forEach { order ->
                    if(item.itemId.equals(order.itemId) && isDateEqual(availability.availableDate, order.orderedDate)){
                        uiElement.orderedQuantity = order.orderedQuantity
                        uiElement.orderUom = order.uom
                        uiElement.orderId = order.orderId
                        uiElement.orderAmount = order.orderedAmount
                        uiElement.discountAmount = order.discountAmount
                        uiElement.orderStatus = order.orderStatus
                        uiElement.paymentStatus = order.paymentStatus
                        uiElement.deliveryStatus = order.deliveryStatus
                    }
                }
                allUiData.add(uiElement)
            }
        }

        allUiData.sortBy { it.itemName }
        allUiData.sortByDescending { it.orderedQuantity }
        return allUiData
    }




    private fun filterVisibleItems(elements: List<DailyOrdersModel>): ArrayList<DailyOrdersModel> {
        var filteredItems = ArrayList<DailyOrdersModel>()
        elements.forEach {element ->
            if (isDateEqual(element.availableDate, df.format(selectedDate))){
                    filteredItems.add(element)
            }
        }
        return filteredItems
    }


    private fun isDateEqual(itemDate: String, selectedDate: String): Boolean {
        return df.format(df.parse(itemDate)).equals(df.format(df.parse(selectedDate)))
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
        _visibleUiData.value = filterVisibleItems(allUiData)
    }

//    fun updateSelectedTimeSlot(timeSlot: String){
//        selectedTimeSlot = timeSlot
//        _allItems.value?.let { _visibleItems.value = filterVisibleItems(it) }
//    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}