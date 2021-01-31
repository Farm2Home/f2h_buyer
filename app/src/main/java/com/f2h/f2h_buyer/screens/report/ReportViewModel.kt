package com.f2h.f2h_buyer.screens.report

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_CONFIRMED
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_DELIVERED
import com.f2h.f2h_buyer.constants.F2HConstants.ORDER_STATUS_ORDERED
import com.f2h.f2h_buyer.constants.F2HConstants.PAYMENT_STATUS_PENDING
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.CommentApi
import com.f2h.f2h_buyer.network.ItemAvailabilityApi
import com.f2h.f2h_buyer.network.OrderApi
import com.f2h.f2h_buyer.network.models.*
import com.f2h.f2h_buyer.screens.group.daily_orders.ServiceOrder
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class ReportViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {


    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private var _reportUiFilterModel = MutableLiveData<ReportUiModel>()
    val reportUiFilterModel: LiveData<ReportUiModel>
        get() = _reportUiFilterModel

    private var _visibleUiData = MutableLiveData<MutableList<ReportItemsHeaderModel>>()
    val visibleUiData: LiveData<MutableList<ReportItemsHeaderModel>>
        get() = _visibleUiData


    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val formatter: DateFormat = SimpleDateFormat("dd-MMM-yyyy")
    private val sessionData = MutableLiveData<SessionEntity>()
    private var allUiData = ArrayList<ReportItemsHeaderModel>()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private val utcFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")


    init {
        getOrdersReportForGroup()
    }

    private fun fetchOrderDate(dateOffset: Int): String {
        var date = Calendar.getInstance()
        date.add(Calendar.DATE, dateOffset)
        return utcFormatter.format(date.time)
    }

    private fun getOrdersReportForGroup() {
        _isProgressBarActive.value = true
        coroutineScope.launch {
            sessionData.value = retrieveSession()
            val getOrdersDataDeferred = OrderApi.retrofitService.getOrderHeadersForGroupUserAndItem(sessionData.value!!.groupId,
                sessionData.value!!.userId, null, fetchOrderDate(-30), fetchOrderDate(30))
            try {
                val orderHeaders = getOrdersDataDeferred.await()

                val availabilityIds = ArrayList<Long>()

                orderHeaders.forEach{ orderHeader ->
                    availabilityIds.addAll(orderHeader.orders.map { x -> x.itemAvailabilityId ?: -1}.distinct())
                }

                val getItemAvailabilitiesDataDeferred =
                    ItemAvailabilityApi.retrofitService.getItemAvailabilities(availabilityIds)

                val itemAvailabilities = getItemAvailabilitiesDataDeferred.await()

                allUiData = createAllUiData(itemAvailabilities, orderHeaders)
                _reportUiFilterModel.value = createAllUiFilters()
                if (allUiData.size > 0) {
                    filterVisibleItems()
                }
            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }


    private fun createAllUiData(itemAvailabilitys: List<ItemAvailability>,
                                orderHeaders: List<OrderHeader>): ArrayList<ReportItemsHeaderModel> {
        val allUiData = ArrayList<ReportItemsHeaderModel>()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<Item> = moshi.adapter(Item::class.java)


        orderHeaders.forEach { orderHeader ->
            val headerUiElement = ReportItemsHeaderModel()
            headerUiElement.deliveryDate = orderHeader.deliveryDate ?: ""
            headerUiElement.orderHeaderId = orderHeader.orderHeaderId?: -1
            headerUiElement.totalAmount = orderHeader.finalAmount?: 0.0
            headerUiElement.packingNumber = orderHeader.packingNumber?: -1

            val uiElements  = ArrayList<ReportItemsModel>()
            orderHeader.orders.forEach {order ->
                val uiElement = ReportItemsModel()

                var item = Item()
                try {
                    item = jsonAdapter.fromJson(order.orderDescription) ?: Item()
                } catch (e: Exception) {
                    Log.e("Parse Error", e.message ?: "")
                }

                // Check item availability for the order. freezed etc
                itemAvailabilitys.forEach { availability ->
                    if (availability.itemAvailabilityId != null) {
                        if (availability.itemAvailabilityId.equals(order.itemAvailabilityId)) {
                            uiElement.isFreezed = availability.isFreezed ?: false
                            uiElement.availableQuantity = availability.availableQuantity ?: 0.0
                        }
                    }
                }

                uiElement.currency = sessionData.value?.groupCurrency ?: ""
                uiElement.itemId = item.itemId ?: -1
                uiElement.itemName = item.itemName ?: ""
                uiElement.itemDescription = item.description ?: ""
                uiElement.itemUom = item.uom ?: ""
                uiElement.sellerName = item.farmerUserName ?: ""
                uiElement.price = item.pricePerUnit ?: 0.0
                uiElement.itemImageLink = item.imageLink ?: ""
                uiElement.orderedDate = df.format(df.parse(order.orderedDate))
                uiElement.orderedQuantity = order.orderedQuantity ?: 0.0
                uiElement.confirmedQuantity = order.confirmedQuantity ?: 0.0
                uiElement.orderId = order.orderId ?: -1L
                uiElement.orderAmount = order.orderedAmount ?: 0.0
                uiElement.discountAmount = order.discountAmount ?: 0.0
                uiElement.orderStatus = order.orderStatus ?: ""
                uiElement.paymentStatus = order.paymentStatus ?: ""
                uiElements.add(uiElement)
            }

            uiElements.sortBy { it.orderId }
            uiElements.sortByDescending { it.orderedQuantity }
            headerUiElement.orders = uiElements
            headerUiElement.serviceOrders = ArrayList()
            orderHeader.serviceOrders.forEach {
                val service = ServiceOrder()
                service.orderId = it.serviceOrderId?:-1
                service.amount = it.amount?:0.0
                service.name = it.name?:""
                service.description = it.description?:""
                headerUiElement.serviceOrders.add(service)
            }

            allUiData.add(headerUiElement)

        }

        return allUiData
    }

    private fun getDisplayQuantity(displayStatus: String, orderedQuantity: Double, confirmedQuantity: Double): Double {
        if (displayStatus.equals("ORDERED")) return orderedQuantity
        return confirmedQuantity
    }


    private fun createAllUiFilters(): ReportUiModel {
        val filters = ReportUiModel()

        filters.itemList = arrayListOf("ALL").plus(allUiData.flatMap { x -> x.orders.map { it.itemName } }
                                                            .distinct().sorted())

        filters.orderStatusList = arrayListOf("ALL", "Open Orders", "Delivered Orders", "Payment Pending")

        filters.paymentStatusList = arrayListOf("ALL").plus(allUiData.flatMap { x ->
                x.orders.filter { uiElement -> !uiElement.paymentStatus.isBlank() }
                        .map { uiElement -> uiElement.paymentStatus }
            }.distinct().sorted())

        filters.farmerNameList = arrayListOf("ALL").plus(allUiData.flatMap { x->
                x.orders.filter { uiElement -> !uiElement.sellerName.isBlank() }
                        .map { uiElement -> uiElement.sellerName }
            }.distinct().sorted())

        filters.timeFilterList = arrayListOf("Today", "Tomorrow", "Next 7 days", "Last 7 days", "Last 15 days", "Last 30 days")

        filters.selectedItem = "ALL"
        filters.selectedPaymentStatus = "ALL"
        filters.selectedOrderStatus = "ALL"
        filters.selectedFarmer = "ALL"
        setTimeFilterRange(0,0) //Today

        return filters
    }


    private fun filterVisibleItems() {
        val elements = allUiData
        val todayDate = Calendar.getInstance()
        val filteredItems = ArrayList<ReportItemsHeaderModel>()
        val selectedItem = reportUiFilterModel.value?.selectedItem ?: ""
        val selectedOrderStatus = reportUiFilterModel.value?.selectedOrderStatus ?: ""
        val selectedPaymentStatus = reportUiFilterModel.value?.selectedPaymentStatus ?: ""
        val selectedStartDate = reportUiFilterModel.value?.selectedStartDate ?: formatter.format(todayDate.time)
        val selectedEndDate = reportUiFilterModel.value?.selectedEndDate ?: formatter.format(todayDate.time)
        val selectedFarmer = reportUiFilterModel.value?.selectedFarmer ?: ""

        elements.forEach { element ->
            if (//(selectedItem == "ALL" || element.itemName.equals(selectedItem)) &&
//                (selectedOrderStatus == "ALL" || selectedOrderStatus.split(",").contains(element.orderStatus)) &&
//                (selectedPaymentStatus == "ALL" || element.paymentStatus.equals(selectedPaymentStatus))  &&
//                (selectedFarmer == "ALL" || element.sellerName.equals(selectedFarmer)) &&
                (isInSelectedDateRange(element, selectedStartDate, selectedEndDate))) {

                //TODO - add date range not just one date
                filteredItems.add(element)
            }
        }
        filteredItems.sortByDescending { df.parse(it.deliveryDate) }
        _visibleUiData.value = filteredItems
    }

    private fun isInSelectedDateRange(
        element: ReportItemsHeaderModel,
        selectedStartDate: String,
        selectedEndDate: String
    ) : Boolean {

        if (element.deliveryDate.isBlank() ||
                selectedEndDate.isBlank() ||
                selectedStartDate.isBlank()) return true

        return df.parse(element.deliveryDate) >= formatter.parse(selectedStartDate) &&
                df.parse(element.deliveryDate) <= formatter.parse(selectedEndDate)
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


    fun onItemSelected(position: Int) {
        _reportUiFilterModel.value?.selectedItem = _reportUiFilterModel.value?.itemList?.get(position) ?: ""
        filterVisibleItems()
    }

    fun onOrderStatusSelected(position: Int) {
        if (position == 0) {
            _reportUiFilterModel.value?.selectedOrderStatus = "ALL"
            _reportUiFilterModel.value?.selectedPaymentStatus = "ALL"
        }
        if (position == 1) {
            _reportUiFilterModel.value?.selectedOrderStatus =
                String.format("%s,%s", ORDER_STATUS_ORDERED, ORDER_STATUS_CONFIRMED)
            _reportUiFilterModel.value?.selectedPaymentStatus = "ALL"
        }
        if (position == 2) {
            _reportUiFilterModel.value?.selectedOrderStatus = ORDER_STATUS_DELIVERED
            _reportUiFilterModel.value?.selectedPaymentStatus = "ALL"
        }
        if (position == 3) {
            _reportUiFilterModel.value?.selectedOrderStatus = "ALL"
            _reportUiFilterModel.value?.selectedPaymentStatus = PAYMENT_STATUS_PENDING
        }
        filterVisibleItems()
    }


    fun onTimeFilterSelected(position: Int) {
        if (position == 0) setTimeFilterRange(0,0) //Today
        if (position == 1) setTimeFilterRange(1,1) //Tomorrow
        if (position == 2) setTimeFilterRange(0,7) //Next 7 Days
        if (position == 3) setTimeFilterRange(-7,0) //Last week
        if (position == 4) setTimeFilterRange(-15,0)  //Last 15 days
        if (position == 5) setTimeFilterRange(-30,0) //Last 30 days
        filterVisibleItems()
    }


    fun onFarmerSelected(position: Int) {
        _reportUiFilterModel.value?.selectedFarmer = _reportUiFilterModel.value?.farmerNameList?.get(position) ?: ""
        filterVisibleItems()
    }

    private fun setTimeFilterRange(startDateOffset: Int, endDateOffset: Int) {
        val rangeStartDate = Calendar.getInstance()
        val rangeEndDate = Calendar.getInstance()
        rangeStartDate.add(Calendar.DATE, startDateOffset)
        rangeEndDate.add(Calendar.DATE, endDateOffset)
        _reportUiFilterModel.value?.selectedStartDate = formatter.format(rangeStartDate.time)
        _reportUiFilterModel.value?.selectedEndDate = formatter.format(rangeEndDate.time)
        filterVisibleItems()
    }

    fun moreDetailsButtonClicked(element: ReportItemsModel) {
        if(element.isMoreDetailsDisplayed){
            element.isMoreDetailsDisplayed = false
            _visibleUiData.value = _visibleUiData.value
            return
        }

        // Do API call to fetch comments
        fetchCommentsForOrder(element)

        element.isMoreDetailsDisplayed = true
        _visibleUiData.value = _visibleUiData.value
    }


    private fun fetchCommentsForOrder(element: ReportItemsModel) {
        setCommentProgressBar(true, element)
        coroutineScope.launch {
            val getCommentsDataDeferred = CommentApi.retrofitService.getComments(element.orderId)
            try {
                val comments: List<Comment> = getCommentsDataDeferred.await()
                element.comments = ArrayList(comments)
                _visibleUiData.value = _visibleUiData.value
            } catch (t: Throwable) {
                println(t.message)
            }
            setCommentProgressBar(false, element)
        }
    }


    private fun setCommentProgressBar(isProgressActive: Boolean, element: ReportItemsModel){
        element.isCommentProgressBarActive = isProgressActive
        _visibleUiData.value = _visibleUiData.value
    }
}