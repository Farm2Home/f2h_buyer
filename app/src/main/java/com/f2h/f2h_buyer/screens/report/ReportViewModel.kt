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
import com.f2h.f2h_buyer.network.ItemAvailabilityApi
import com.f2h.f2h_buyer.network.OrderApi
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.Item
import com.f2h.f2h_buyer.network.models.ItemAvailability
import com.f2h.f2h_buyer.network.models.Order
import com.f2h.f2h_buyer.network.models.UserDetails
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

    private var _visibleUiData = MutableLiveData<MutableList<ReportItemsModel>>()
    val visibleUiData: LiveData<MutableList<ReportItemsModel>>
        get() = _visibleUiData


    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val formatter: DateFormat = SimpleDateFormat("dd-MMM-yyyy")
    private val sessionData = MutableLiveData<SessionEntity>()
    private var allUiData = ArrayList<ReportItemsModel>()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getOrdersReportForGroup()
    }

    fun getOrdersReportForGroup() {
        _isProgressBarActive.value = true
        coroutineScope.launch {
            sessionData.value = retrieveSession()
            var getOrdersDataDeferred = OrderApi.retrofitService.getOrdersForGroupUserAndItem(sessionData.value!!.groupId, sessionData.value!!.userId, null, null, null)
            try {
                var orders = getOrdersDataDeferred.await()
                var userIds = orders.map { x -> x.buyerUserId ?: -1}
                    .plus(orders.map { x -> x.sellerUserId ?: -1}).distinct()
                var availabilityIds = orders.map { x -> x.itemAvailabilityId ?: -1 }

                var getUserDetailsDataDeferred =
                    UserApi.retrofitService.getUserDetailsByUserIds(userIds)

                var getItemAvailabilitiesDataDeferred =
                    ItemAvailabilityApi.retrofitService.getItemAvailabilities(availabilityIds)

                var itemAvailabilities = getItemAvailabilitiesDataDeferred.await()
                var userDetailsList = getUserDetailsDataDeferred.await()

                allUiData = createAllUiData(itemAvailabilities, orders, userDetailsList)
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
                                orders: List<Order>, userDetailsList: List<UserDetails>): ArrayList<ReportItemsModel> {
        var allUiData = ArrayList<ReportItemsModel>()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<Item> = moshi.adapter(Item::class.java)
        orders.forEach { order ->

            var uiElement = ReportItemsModel()
            var item = Item()
            try {
                item = jsonAdapter.fromJson(order.orderDescription) ?: Item()
            } catch (e: Exception){
                Log.e("Parse Error", e.message)
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

            if (item != null) {
                uiElement.itemId = item.itemId ?: -1
                uiElement.itemName = item.itemName ?: ""
                uiElement.itemDescription = item.description ?: ""
                uiElement.itemUom = item.uom ?: ""
                uiElement.itemImageLink = item.imageLink ?: ""
                uiElement.price = item.pricePerUnit ?: 0.0
            }
            uiElement.orderedDate = formatter.format(df.parse(order.orderedDate))
            uiElement.orderedQuantity = order.orderedQuantity ?: 0.0
            uiElement.confirmedQuantity = order.confirmedQuantity ?: 0.0
            uiElement.orderId = order.orderId ?: -1L
            uiElement.orderAmount = order.orderedAmount ?: 0.0
            uiElement.discountAmount = order.discountAmount ?: 0.0
            uiElement.orderStatus = order.orderStatus ?: ""
            uiElement.paymentStatus = order.paymentStatus ?: ""
            uiElement.orderComment = order.orderComment ?: ""
            uiElement.buyerName = userDetailsList.filter { x -> x.userId?.equals(order.buyerUserId) ?: false }.single().userName ?: ""
            uiElement.sellerName = userDetailsList.filter { x -> x.userId?.equals(order.sellerUserId) ?: false }.single().userName ?: ""
            uiElement.deliveryAddress = order.deliveryLocation ?: ""
            uiElement.displayQuantity = getDisplayQuantity(uiElement.orderStatus, uiElement.orderedQuantity, uiElement.confirmedQuantity)
            allUiData.add(uiElement)
        }

        allUiData.sortByDescending { formatter.parse(it.orderedDate) }
        return allUiData
    }

    private fun getDisplayQuantity(displayStatus: String, orderedQuantity: Double, confirmedQuantity: Double): Double {
        if (displayStatus.equals("ORDERED")) return orderedQuantity
        return confirmedQuantity
    }


    private fun createAllUiFilters(): ReportUiModel {
        var filters = ReportUiModel()

        filters.itemList = arrayListOf("ALL").plus(allUiData.sortedBy { uiElement -> uiElement.itemName }
            .filter { uiElement -> !uiElement.itemName.isNullOrBlank() }
            .map { uiElement -> uiElement.itemName }.distinct().sorted())

        filters.orderStatusList = arrayListOf("ALL", "Open Orders", "Delivered Orders", "Payment Pending")

        filters.paymentStatusList = arrayListOf("ALL").plus(allUiData.sortedBy { uiElement -> uiElement.paymentStatus }
            .filter { uiElement -> !uiElement.paymentStatus.isNullOrBlank() }
            .map { uiElement -> uiElement.paymentStatus }.distinct().sorted())

        filters.buyerNameList = allUiData.sortedBy { uiElement -> uiElement.buyerName }
            .filter { uiElement -> !uiElement.buyerName.isNullOrBlank() }
            .map { uiElement -> uiElement.buyerName }.distinct().sorted()

        filters.farmerNameList = arrayListOf("ALL").plus(allUiData.sortedBy { uiElement -> uiElement.sellerName }
            .filter { uiElement -> !uiElement.sellerName.isNullOrBlank() }
            .map { uiElement -> uiElement.sellerName }.distinct().sorted())

        filters.timeFilterList = arrayListOf("Today", "Tomorrow", "Next 7 days", "Last 7 days", "Last 15 days", "Last 30 days")

        filters.selectedItem = "ALL"
        filters.selectedPaymentStatus = "ALL"
        filters.selectedOrderStatus = "ALL"
        filters.selectedFarmer = "ALL"
        filters.selectedBuyer = filters.buyerNameList.first()
        setTimeFilterRange(0,0) //Today

        return filters
    }


    private fun filterVisibleItems() {
        val elements = allUiData
        var todayDate = Calendar.getInstance()
        var filteredItems = ArrayList<ReportItemsModel>()
        var selectedItem = reportUiFilterModel.value?.selectedItem ?: ""
        var selectedOrderStatus = reportUiFilterModel.value?.selectedOrderStatus ?: ""
        var selectedPaymentStatus = reportUiFilterModel.value?.selectedPaymentStatus ?: ""
        var selectedStartDate = reportUiFilterModel.value?.selectedStartDate ?: formatter.format(todayDate.time)
        var selectedEndDate = reportUiFilterModel.value?.selectedEndDate ?: formatter.format(todayDate.time)
        var selectedBuyer = reportUiFilterModel.value?.selectedBuyer ?: ""
        var selectedFarmer = reportUiFilterModel.value?.selectedFarmer ?: ""

        elements.forEach { element ->
            if ((selectedItem == "ALL" || element.itemName.equals(selectedItem)) &&
                (selectedOrderStatus == "ALL" || selectedOrderStatus.split(",").contains(element.orderStatus)) &&
                (selectedPaymentStatus == "ALL" || element.paymentStatus.equals(selectedPaymentStatus))  &&
                (selectedBuyer == "ALL" || element.buyerName.equals(selectedBuyer)) &&
                (selectedFarmer == "ALL" || element.sellerName.equals(selectedFarmer)) &&
                (isInSelectedDateRange(element, selectedStartDate, selectedEndDate))) {

                //TODO - add date range not just one date
                filteredItems.add(element)
            }
        }
        filteredItems.sortByDescending { formatter.parse(it.orderedDate) }
        _visibleUiData.value = filteredItems
    }

    private fun isInSelectedDateRange(
        element: ReportItemsModel,
        selectedStartDate: String,
        selectedEndDate: String
    ) : Boolean {

        if (element.orderedDate.isNullOrBlank() ||
                selectedEndDate.isNullOrBlank() ||
                selectedStartDate.isNullOrBlank()) return true

        return formatter.parse(element.orderedDate) >= formatter.parse(selectedStartDate) &&
                formatter.parse(element.orderedDate) <= formatter.parse(selectedEndDate)
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
        if (position.equals(0)) setTimeFilterRange(0,0) //Today
        if (position.equals(1)) setTimeFilterRange(1,1) //Tomorrow
        if (position.equals(2)) setTimeFilterRange(0,7) //Next 7 Days
        if (position.equals(3)) setTimeFilterRange(-7,0) //Last week
        if (position.equals(4)) setTimeFilterRange(-15,0)  //Last 15 days
        if (position.equals(5)) setTimeFilterRange(-30,0) //Last 30 days
        filterVisibleItems()
    }

    fun onBuyerSelected(position: Int) {
        _reportUiFilterModel.value?.selectedBuyer = _reportUiFilterModel.value?.buyerNameList?.get(position) ?: ""
        filterVisibleItems()
    }

    fun onFarmerSelected(position: Int) {
        _reportUiFilterModel.value?.selectedFarmer = _reportUiFilterModel.value?.farmerNameList?.get(position) ?: ""
        filterVisibleItems()
    }

    fun setTimeFilterRange(startDateOffset: Int, endDateOffset: Int) {
        var rangeStartDate = Calendar.getInstance()
        var rangeEndDate = Calendar.getInstance()
        rangeStartDate.add(Calendar.DATE, startDateOffset)
        rangeEndDate.add(Calendar.DATE, endDateOffset)
        _reportUiFilterModel.value?.selectedStartDate = formatter.format(rangeStartDate.time)
        _reportUiFilterModel.value?.selectedEndDate = formatter.format(rangeEndDate.time)
        filterVisibleItems()
    }
}