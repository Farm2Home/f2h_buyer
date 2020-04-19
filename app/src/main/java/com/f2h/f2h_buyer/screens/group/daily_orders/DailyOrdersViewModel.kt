package com.f2h.f2h_buyer.screens.group.daily_orders

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.ItemApi
import com.f2h.f2h_buyer.network.ItemAvailabilityApi
import com.f2h.f2h_buyer.network.OrderApi
import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.network.models.ItemAvailability
import com.f2h.f2h_buyer.network.models.Order
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DailyOrdersViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private var _visibleUiData = MutableLiveData<MutableList<DailyOrdersUiModel>>()
    val visibleUiData: LiveData<MutableList<DailyOrdersUiModel>>
        get() = _visibleUiData

    private val sessionData = MutableLiveData<SessionEntity>()
    private var selectedDate = Calendar.getInstance().time


    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var allUiData = ArrayList<DailyOrdersUiModel>()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _isProgressBarActive.value = true
        getItemsAndAvailabilitiesForGroup()
    }


    private fun getItemsAndAvailabilitiesForGroup() {
        coroutineScope.launch {
            sessionData.value = retrieveSession()
            var getOrdersDataDeferred = OrderApi.retrofitService.getOrdersForGroupAndUser(sessionData.value!!.groupId, sessionData.value!!.userId)
            try {
                var orders = getOrdersDataDeferred.await()
                var availabilityIds: ArrayList<Long> = arrayListOf()
                orders.forEach { order ->
                    availabilityIds.add(order.itemAvailabilityId ?: -1)
                }
                var getItemAvailabilitiesDataDeferred = ItemAvailabilityApi.retrofitService.getItemAvailabilities(availabilityIds)
                var itemAvailabilities = getItemAvailabilitiesDataDeferred.await()
                allUiData = createAllUiData(itemAvailabilities, orders)
                if (allUiData.size > 0) {
                    _visibleUiData.value = filterVisibleItems(allUiData)
                }
            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }



    private fun createAllUiData(itemAvailabilitys: List<ItemAvailability>, orders: List<Order>): ArrayList<DailyOrdersUiModel> {
        var allUiData = ArrayList<DailyOrdersUiModel>()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<Item> = moshi.adapter<Item>(Item::class.java)

        orders.forEach { order ->
            var uiElement = DailyOrdersUiModel()
            var item: Item = Item()
            try {
                item = jsonAdapter.fromJson(order.orderDescription) ?: Item()
            } catch (e: Exception){
                Log.e("Parse Error", e.message)
            }

            // Check item availability for the order. freezed etc
            itemAvailabilitys.forEach { availability ->
                if (availability.itemAvailabilityId.equals(order.itemAvailabilityId)) {
                    uiElement.isFreezed = availability.isFreezed
                }
            }

            if (item != null) {
                uiElement.itemId = item.itemId
                uiElement.itemName = item.itemName
                uiElement.itemDescription = item.description
                uiElement.itemUom = item.uom
                uiElement.farmerName = item.farmerUserName
                uiElement.price = item.pricePerUnit
            }
            uiElement.orderedDate = df.format(df.parse(order.orderedDate))
            uiElement.orderedQuantity = order.orderedQuantity ?: 0F
            uiElement.orderUom = order.uom ?: ""
            uiElement.orderId = order.orderId ?: -1L
            uiElement.orderAmount = order.orderedAmount ?: 0F
            uiElement.discountAmount = order.discountAmount ?: 0F
            uiElement.orderStatus = order.orderStatus ?: ""
            uiElement.paymentStatus = order.paymentStatus ?: ""
            uiElement.deliveryStatus = order.deliveryStatus ?: ""

            allUiData.add(uiElement)
        }

        allUiData.sortBy { it.itemName }
        allUiData.sortByDescending { it.orderedQuantity }
        return allUiData
    }



    private fun filterVisibleItems(elements: List<DailyOrdersUiModel>): ArrayList<DailyOrdersUiModel> {
        var filteredItems = ArrayList<DailyOrdersUiModel>()
        elements.forEach {element ->
            if (isDateEqual(element.orderedDate, df.format(selectedDate))){
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