package com.f2h.f2h_buyer.screens.group.pre_order

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
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat

class PreOrderViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private val _item = MutableLiveData<Item>()
    val item: LiveData<Item>
        get() = _item

    private val _table = MutableLiveData<List<PreOrderModel>>()
    val table: LiveData<List<PreOrderModel>>
        get() = _table

    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var sessionData = SessionEntity()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _isProgressBarActive.value = true
    }


    fun getItemAndAvailabilities(itemId: Long) {
        coroutineScope.launch {
            sessionData = retrieveSession()
            var getItemDataDeferred = ItemApi.retrofitService.getItem(itemId)
            var getOrdersDataDeferred = OrderApi.retrofitService.getOrdersForGroupAndUser(sessionData.groupId, sessionData.userId)
            try {
                var item = getItemDataDeferred.await()
                var orders = ArrayList(getOrdersDataDeferred.await())
                orders.retainAll { x -> x.itemAvailabilityId == item.itemId }

                if (item.itemId != 0L) {
                    _item.value = item
                    _table.value = createTableRows(item, orders)
                }
            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }

    private fun createTableRows(item: Item, orders: ArrayList<Order>): ArrayList<PreOrderModel> {
        var list = ArrayList<PreOrderModel>()
        item.itemAvailability.forEach { availability ->
            var order = orders.filter { x -> isDateEqual(x.orderedDate ?: "", availability.availableDate ?: "") }
            if (order.isEmpty()){
                order = listOf(Order())
            }
            var row = PreOrderModel()
            row.itemAvailabilityId = availability.itemAvailabilityId ?: 0L
            row.availableDate = formatDate(availability.availableDate ?: "")
            row.orderedQuantity = order[0].orderedQuantity ?: (0).toDouble()
            row.orderUom = order[0].uom ?: ""
            list.add(row)
        }
        list.sortBy { it.availableDate }
        return list
    }


    private fun isDateEqual(itemDate: String, selectedDate: String): Boolean {
        return df.format(df.parse(itemDate)).equals(df.format(df.parse(selectedDate)))
    }


    private fun formatDate(availableDate: String): String {
        val parser: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formatter: DateFormat = SimpleDateFormat("MMM-dd-yyy")
        var formattedDate: String = ""
        try {
            var date = availableDate
            formattedDate = formatter.format(parser.parse(date))
        } catch (e: Exception) {
            println(e)
        }
        return formattedDate
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
}