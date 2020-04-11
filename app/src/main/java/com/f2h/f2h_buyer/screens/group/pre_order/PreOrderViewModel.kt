package com.f2h.f2h_buyer.screens.group.pre_order

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

    private val _table = MutableLiveData<List<TableComponent>>()
    val table: LiveData<List<TableComponent>>
        get() = _table

    private val _sessionData = MutableLiveData<SessionEntity>()
    val sessionData: LiveData<SessionEntity>
        get() = _sessionData

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _isProgressBarActive.value = true
    }


    fun getItemAndAvailabilities(itemId: Long) {
        coroutineScope.launch {
            _sessionData.value = retrieveSession()
            var getItemDataDeferred = ItemApi.retrofitService.getItem(itemId)
            try {
                var item = getItemDataDeferred.await()
                if (item.itemId != 0L) {
                    _item.value = item
                    _table.value = createTableRows(item)
                }
            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }

    private fun createTableRows(item: Item): ArrayList<TableComponent> {
        var list = ArrayList<TableComponent>()
        list.add(TableComponent(id = 0L, date = "Available Dates", quantity = "Order"))
        item.itemAvailability.forEach { availability ->
            var row = TableComponent()
            row.id = availability.itemAvailabilityId
            row.quantity = availability.committedQuantity.toString()
            row.date = formatDate(availability.availableDate)
            list.add(row)
        }
        return list
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