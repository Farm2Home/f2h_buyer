package com.f2h.f2h_buyer.screens.group.pre_order

import android.app.Application
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
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PreOrderViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private val _preOrderItems = MutableLiveData<ArrayList<PreOrderItemsModel>>()
    val preOrderItems: LiveData<ArrayList<PreOrderItemsModel>>
        get() = _preOrderItems

    private val _preOrderUiModel = MutableLiveData<PreOrderUiModel>()
    val preOrderUiModel: LiveData<PreOrderUiModel>
        get() = _preOrderUiModel

    private val df_iso: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'")
    private val preOrderDaysMax = 10
    private var startDate = ""
    private var endDate = ""

    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var sessionData = SessionEntity()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _isProgressBarActive.value = true
        setPreOrderDateRange()
    }


    fun fetchAllData(itemId: Long) {
        coroutineScope.launch {
            sessionData = retrieveSession()
            try {

                // Fetch Item Data
                val getItemDataDeferred = ItemApi.retrofitService.getItem(itemId)
                val item = getItemDataDeferred.await()

                //Fetch existing Orders Data
                val getOrdersDataDeferred = OrderApi.retrofitService.getOrdersForGroupUserAndItem(sessionData.groupId,
                    sessionData.userId, item.itemId!!, startDate, endDate)
                val orders = ArrayList(getOrdersDataDeferred.await())

                //Fetch all availabilities for the item
                val getItemAvailabilitiesDeferred = ItemAvailabilityApi.retrofitService.getItemAvailabilitiesByItemId(item.itemId!!)
                val itemAvailabilities = ArrayList(getItemAvailabilitiesDeferred.await())

                //Create the UI Model to populate UI
                _preOrderItems.value = createPreOrderUiElements(item, orders, itemAvailabilities)

            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }


    private fun createPreOrderUiElements(item: Item, orders: ArrayList<Order>, itemAvailabilities: ArrayList<ItemAvailability>): ArrayList<PreOrderItemsModel> {
        var list = arrayListOf<PreOrderItemsModel>()

        var uiModel = PreOrderUiModel()
        uiModel.itemId = item.itemId ?: -1
        uiModel.itemName = item.itemName ?: ""
        uiModel.itemDescription = item.description ?: ""
        uiModel.itemImageLink = item.imageLink ?: ""
        uiModel.itemPrice = item.pricePerUnit ?: 0.0
        uiModel.itemUom = item.uom ?: ""
        uiModel.farmerName = item.farmerUserName ?: ""
        _preOrderUiModel.value = uiModel

        itemAvailabilities.filter { compareDates(it.availableDate, startDate) >= 0 &&
                compareDates(it.availableDate, endDate) <= 0 }
            .forEach { availability ->
                var preOrderItem = PreOrderItemsModel()
                preOrderItem.itemAvailabilityId = availability.itemAvailabilityId ?: -1L
                preOrderItem.availableDate = availability.availableDate ?: ""
                preOrderItem.availableTimeSlot = availability.availableTimeSlot ?: ""
                preOrderItem.availableQuantity = availability.availableQuantity ?: 0.0
                preOrderItem.isFreezed = availability.isFreezed ?: false
                preOrderItem.itemUom = item.uom ?: ""

                var order = orders.filter { it.itemAvailabilityId!!.equals(availability.itemAvailabilityId) }
                if (order.isNotEmpty()) {
                    preOrderItem.orderedQuantity = order.first().orderedQuantity ?: 0.0
                    preOrderItem.confirmedQuantity = order.first().confirmedQuantity ?: 0.0
                    preOrderItem.orderStatus = order.first().orderStatus ?: ""
                    preOrderItem.deliveryStatus = order.first().deliveryStatus ?: ""
                    preOrderItem.orderUom = order.first().uom ?: ""
                    preOrderItem.orderId = order.first().orderId ?: -1L
                }
                list.add(preOrderItem)
            }

        list.sortBy { it.availableDate }
        return list
    }


    private fun compareDates(date1: String?, date2: String?): Long {
        if (date1 == null) return -1
        if (date2 == null) return 1

        val d1 = df.parse(date1).time
        val d2 = df.parse(date2).time
        return d1-d2
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


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    private fun setPreOrderDateRange() {
        startDate = getStartDate()
        endDate = getEndDate()
    }

    private fun getStartDate(): String {
        val date: Calendar = Calendar.getInstance()
        val startDate: String = df_iso.format(date.time)
        return startDate
    }


    private fun getEndDate(): String {
        val date: Calendar = Calendar.getInstance()
        date.add(Calendar.DATE, preOrderDaysMax)
        val endDate: String = df_iso.format(date.time)
        return endDate
    }

}