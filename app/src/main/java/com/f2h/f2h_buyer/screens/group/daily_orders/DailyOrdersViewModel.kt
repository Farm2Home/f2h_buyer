package com.f2h.f2h_buyer.screens.group.daily_orders

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.CommentApi
import com.f2h.f2h_buyer.network.ItemAvailabilityApi
import com.f2h.f2h_buyer.network.OrderApi
import com.f2h.f2h_buyer.network.models.*
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

    private var _visibleUiData = MutableLiveData<MutableList<DailyOrderHeaderUiModel>>()
    val visibleUiData: LiveData<MutableList<DailyOrderHeaderUiModel>>
        get() = _visibleUiData

    private var _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val sessionData = MutableLiveData<SessionEntity>()


    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var allUiData = ArrayList<DailyOrderHeaderUiModel>()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _isProgressBarActive.value = true
        getItemsAndAvailabilitiesForUser()
    }


    fun getItemsAndAvailabilitiesForUser() {
        _isProgressBarActive.value = true
        coroutineScope.launch {
            sessionData.value = retrieveSession()
            val getOrdersDataDeferred = OrderApi.retrofitService.getOrderHeadersForGroupUserAndItem(sessionData.value!!.groupId,
                sessionData.value!!.userId, null, todayDate(), null)
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
                _visibleUiData.value = filterVisibleItems(allUiData)
            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }



    private fun createAllUiData(itemAvailabilitys: List<ItemAvailability>, orderHeaders: List<OrderHeader>): ArrayList<DailyOrderHeaderUiModel> {
        val allUiData = ArrayList<DailyOrderHeaderUiModel>()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<Item> = moshi.adapter(Item::class.java)


        orderHeaders.forEach { orderHeader ->
            val headerUiElement = DailyOrderHeaderUiModel()
            headerUiElement.deliveryDate = orderHeader.deliveryDate ?: ""
            headerUiElement.orderHeaderId = orderHeader.orderHeaderId?: -1
            headerUiElement.totalAmount = orderHeader.finalAmount?: 0.0
            headerUiElement.packingNumber = orderHeader.packingNumber?: -1

            val uiElements  = ArrayList<DailyOrders>()
            orderHeader.orders.forEach {order ->
                val uiElement = DailyOrders()

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

                if (item != null) {
                    uiElement.itemId = item.itemId ?: -1
                    uiElement.itemName = item.itemName ?: ""
                    uiElement.itemDescription = item.description ?: ""
                    uiElement.itemUom = item.uom ?: ""
                    uiElement.farmerName = item.farmerUserName ?: ""
                    uiElement.price = item.pricePerUnit ?: 0.0
                    uiElement.orderQtyJump = item.orderQtyJump ?: 0.0
                    uiElement.itemImageLink = item.imageLink ?: ""
                }
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



    private fun filterVisibleItems(elements: ArrayList<DailyOrderHeaderUiModel>): ArrayList<DailyOrderHeaderUiModel> {
        elements.sortByDescending { df.parse(it.deliveryDate).time }
        return elements
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


    // increase order qty till max available qty
    fun increaseOrderQuantity(uiElement: DailyOrders){
//        _visibleUiData.value?.forEach { uiElement ->
//            if (uiElement.orderId.equals(updateElement.orderId)){
//                uiElement.orderedQuantity = uiElement.orderedQuantity.plus(uiElement.orderQtyJump)
//                uiElement.quantityChange = uiElement.quantityChange.plus(uiElement.orderQtyJump)
//
//                // logic to prevent increasing quantity beyond maximum
//                if (uiElement.quantityChange > uiElement.availableQuantity) {
//                    uiElement.orderedQuantity = uiElement.orderedQuantity.minus(uiElement.orderQtyJump)
//                    uiElement.quantityChange = uiElement.quantityChange.minus(uiElement.orderQtyJump)
//                    _toastMessage.value = "No more stock"
//                }
//
//                uiElement.orderAmount = calculateOrderAmount(uiElement)
//            }
//        }

        uiElement.orderedQuantity = uiElement.orderedQuantity.plus(uiElement.orderQtyJump)
        uiElement.quantityChange = uiElement.quantityChange.plus(uiElement.orderQtyJump)

        // logic to prevent increasing quantity beyond maximum
        if (uiElement.quantityChange > uiElement.availableQuantity) {
            uiElement.orderedQuantity = uiElement.orderedQuantity.minus(uiElement.orderQtyJump)
            uiElement.quantityChange = uiElement.quantityChange.minus(uiElement.orderQtyJump)
            _toastMessage.value = "No more stock"
        }

        uiElement.orderAmount = calculateOrderAmount(uiElement)
        _visibleUiData.value = _visibleUiData.value
    }


    // decrease order qty till min 0
    fun decreaseOrderQuantity(uiElement: DailyOrders){
//        _visibleUiData.value?.forEach { uiElement ->
//            if (uiElement.orderId.equals(updateElement.orderId)){
//                uiElement.orderedQuantity = uiElement.orderedQuantity.minus(uiElement.orderQtyJump)
//                uiElement.quantityChange = uiElement.quantityChange.minus(uiElement.orderQtyJump)
//                if (uiElement.orderedQuantity < 0) uiElement.orderedQuantity = 0.0
//                uiElement.orderAmount = calculateOrderAmount(uiElement)
//            }
//        }
        uiElement.orderedQuantity = uiElement.orderedQuantity.minus(uiElement.orderQtyJump)
        uiElement.quantityChange = uiElement.quantityChange.minus(uiElement.orderQtyJump)
        if (uiElement.orderedQuantity < 0) uiElement.orderedQuantity = 0.0
        uiElement.orderAmount = calculateOrderAmount(uiElement)

        _visibleUiData.value = _visibleUiData.value
    }

    fun moreDetailsButtonClicked(element: DailyOrders) {
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

    private fun fetchCommentsForOrder(element: DailyOrders) {
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


    fun onSendCommentButtonClicked(element: DailyOrders){
        if(element.newComment.isNullOrBlank()){
            return
        }

        var request = CommentCreateRequest(
            comment = element.newComment,
            commenter = sessionData.value?.userName ?: "",
            commenterUserId = sessionData.value?.userId ?: -1,
            orderId = element.orderId,
            createdBy = sessionData.value?.userName ?: "",
            updatedBy = sessionData.value?.userName ?: ""
        )

        setCommentProgressBar(true, element)
        coroutineScope.launch {
            val createCommentsDataDeferred = CommentApi.retrofitService.createComment(request)
            try{
                createCommentsDataDeferred.await()
                // Do API call to refresh comments
                fetchCommentsForOrder(element)
                clearCommentTypeBox(element)
            } catch (t:Throwable){
                println(t.message)
            }
            setCommentProgressBar(false, element)
        }

    }


    private fun setCommentProgressBar(isProgressActive: Boolean, element: DailyOrders){
        element.isCommentProgressBarActive = isProgressActive
        _visibleUiData.value = _visibleUiData.value
    }

    private fun clearCommentTypeBox(element: DailyOrders){
        element.newComment = ""
        _visibleUiData.value = _visibleUiData.value
    }

    private fun calculateOrderAmount(uiElement: DailyOrders): Double {
        return uiElement.orderedQuantity.times(uiElement.price)
    }


    fun onClickSaveButton() {
        _isProgressBarActive.value = true
        var orderUpdates = arrayListOf<OrderUpdateRequest>()
        _visibleUiData.value?.forEach { header ->
            header.orders.filter { it -> it.orderStatus.equals("ORDERED") }.forEach { uiElement ->
                var orderUpdate = OrderUpdateRequest(
                    orderId = uiElement.orderId,
                    orderStatus = uiElement.orderStatus,
                    discountAmount = uiElement.discountAmount,
                    orderedAmount = uiElement.orderAmount,
                    orderedQuantity = uiElement.orderedQuantity,
                    paymentStatus = uiElement.paymentStatus
                )
                orderUpdates.add(orderUpdate)
            }
        }

        coroutineScope.launch {
            var updateOrdersDataDeferred = OrderApi.retrofitService.updateOrders(orderUpdates)
            try{
                updateOrdersDataDeferred.await()
                getItemsAndAvailabilitiesForUser()
            } catch (t:Throwable){
                println(t.message)
                _toastMessage.value = "Out of stock"
            }
            _isProgressBarActive.value = false
        }
    }


    fun todayDate(): String {
        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'")
        var today = Calendar.getInstance()
        return formatter.format(today.time)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}