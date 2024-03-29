package com.f2h.f2h_buyer.screens.group.pre_order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.OrderApi
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.*
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

    private var _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private var _orderSuccessful = MutableLiveData<Boolean>()
    val orderSuccessful: LiveData<Boolean>
        get() = _orderSuccessful

    private val df_iso: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'")
    private val preOrderDaysMax = 10
    private var startDate = ""
    private var endDate = ""
    private var selectedItem = Item()
    private var farmerDetails = listOf<UserDetails>()

    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var sessionData = SessionEntity()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        setPreOrderDateRange()
        createPreOrderUiElements(Item(), arrayListOf())
        createPreOrderUiModel(Item(), arrayListOf())
    }

    fun setItemAndFarmer(item: Item){
        selectedItem = item
        coroutineScope.launch {
            sessionData = retrieveSession()
            try {
                val getUserDetailsDataDeferred = UserApi.retrofitService
                    .getUserDetailsByUserIds(arrayListOf(item.farmerUserId ?: -1))
                farmerDetails = getUserDetailsDataDeferred.await()
                createPreOrderUiModel(selectedItem, farmerDetails)

            } catch (t:Throwable){
                println(t.message)
            }
        }

    }


    fun fetchOrderData() {
        _orderSuccessful.value = false
        _isProgressBarActive.value = true
        coroutineScope.launch {
            sessionData = retrieveSession()
            try {
                //Fetch existing Orders Data
                val getOrdersDataDeferred = OrderApi.retrofitService.getOrdersForGroupUserAndItem(selectedItem.groupId ?: 0,
                    sessionData.userId, selectedItem.itemId, startDate, endDate)
                val orders = ArrayList(getOrdersDataDeferred.await())

                //Create the UI Model to populate UI
                _preOrderItems.value = createPreOrderUiElements(selectedItem, orders)

            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }

    private fun createPreOrderUiModel(item: Item, farmerDetails: List<UserDetails>){
        val uiModel = PreOrderUiModel()
        uiModel.currency = sessionData.groupCurrency
        uiModel.itemId = item.itemId ?: -1
        uiModel.itemName = item.itemName ?: ""
        uiModel.itemDescription = item.description ?: ""
        uiModel.itemImageLink = item.imageLink ?: ""
        uiModel.itemPrice = item.pricePerUnit ?: 0.0
        uiModel.itemUom = item.uom ?: ""
        uiModel.farmerName = item.farmerUserName ?: ""
        uiModel.farmerMobile = farmerDetails.firstOrNull()?.mobile ?: ""
        _preOrderUiModel.value = uiModel
    }

    private fun createPreOrderUiElements(item: Item, orders: ArrayList<Order>): ArrayList<PreOrderItemsModel> {
        val list = arrayListOf<PreOrderItemsModel>()

        item.itemAvailability.filter { compareDates(it.availableDate, startDate) >= 0 &&
                compareDates(it.availableDate, endDate) <= 0 }
            .forEach { availability ->
                val preOrderItem = PreOrderItemsModel()
                preOrderItem.currency = sessionData.groupCurrency
                preOrderItem.itemAvailabilityId = availability.itemAvailabilityId ?: -1L
                preOrderItem.availableDate = availability.availableDate ?: ""
                preOrderItem.availableTimeSlot = availability.availableTimeSlot ?: ""
                preOrderItem.availableQuantity = availability.availableQuantity ?: 0.0
                preOrderItem.isFreezed = availability.isFreezed ?: false
                preOrderItem.itemUom = item.uom ?: ""
                preOrderItem.orderQuantityJump = item.orderQtyJump ?: 0.0
                preOrderItem.minimumQuantity = item.minimumQty ?: 0.0

//                orderHeaders.forEach { orderHeaders
                val order = orders.filter { it.itemAvailabilityId!! == availability.itemAvailabilityId }
                if (order.isNotEmpty()) {
                    preOrderItem.orderedQuantity = order.first().orderedQuantity ?: 0.0
                    preOrderItem.confirmedQuantity = order.first().confirmedQuantity ?: 0.0
                    preOrderItem.orderStatus = order.first().orderStatus ?: ""
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
        return df_iso.format(date.time)
    }


    private fun getEndDate(): String {
        val date: Calendar = Calendar.getInstance()
        date.add(Calendar.DATE, preOrderDaysMax)
        return df_iso.format(date.time)
    }



    // increase order qty till max available qty
    fun increaseOrderQuantity(selectedPreOrder: PreOrderItemsModel){
        _preOrderItems.value?.forEach { preOrderUiElement ->
            if (preOrderUiElement.itemAvailabilityId.equals(selectedPreOrder.itemAvailabilityId)){
                var increaseQty = preOrderUiElement.orderQuantityJump
                if (preOrderUiElement.minimumQuantity>0.0 && preOrderUiElement.orderedQuantity < preOrderUiElement.minimumQuantity){
                    increaseQty = preOrderUiElement.minimumQuantity - preOrderUiElement.orderedQuantity
                    if (increaseQty > preOrderUiElement.availableQuantity){
                        increaseQty = preOrderUiElement.availableQuantity
                    }
                }

                preOrderUiElement.orderedQuantity =
                    preOrderUiElement.orderedQuantity.plus(increaseQty)
                preOrderUiElement.quantityChange =
                    preOrderUiElement.quantityChange.plus(increaseQty)

                // logic to prevent increasing quantity beyond maximum
                if (preOrderUiElement.quantityChange > preOrderUiElement.availableQuantity) {
                    preOrderUiElement.orderedQuantity = preOrderUiElement.orderedQuantity.minus(increaseQty)
                    preOrderUiElement.quantityChange = preOrderUiElement.quantityChange.minus(increaseQty)
                    _toastMessage.value = "No more stock"
                }
            }
        }
        _preOrderItems.value = _preOrderItems.value
    }


    // decrease order qty till min 0
    fun decreaseOrderQuantity(selectedPreOrder: PreOrderItemsModel){
        _preOrderItems.value?.forEach { preOrderUiElement ->
            var decreaseQty = preOrderUiElement.orderQuantityJump
            if (preOrderUiElement.itemAvailabilityId.equals(selectedPreOrder.itemAvailabilityId)){
                if (preOrderUiElement.minimumQuantity>0.0 &&
                    preOrderUiElement.orderedQuantity <= preOrderUiElement.minimumQuantity){
                    decreaseQty = preOrderUiElement.orderedQuantity
                }

                preOrderUiElement.orderedQuantity =
                    preOrderUiElement.orderedQuantity.minus(decreaseQty)
                preOrderUiElement.quantityChange =
                    preOrderUiElement.quantityChange.minus(decreaseQty)

                if (preOrderUiElement.orderedQuantity < 0) {
                    preOrderUiElement.orderedQuantity = 0.0
                    preOrderUiElement.quantityChange = preOrderUiElement.quantityChange.plus(decreaseQty)
                }
            }
        }
        _preOrderItems.value = _preOrderItems.value
    }

    fun onClickSaveButton() {
        _isProgressBarActive.value = true
        val newOrders: ArrayList<OrderCreateRequest> = arrayListOf()
        val updatedOrders: ArrayList<OrderUpdateRequest> = arrayListOf()
        _preOrderItems.value?.forEach { preOrder ->

            if(preOrder.quantityChange.equals(0.0)){
                return@forEach
            }

            if (preOrder.orderId <= 0){
                newOrders.add(createNewOrder(preOrder))
            }

            if (preOrder.orderId > 0) {
                updatedOrders.add(createUpdateOrder(preOrder))
            }
        }

        coroutineScope.launch {
            val updateOrdersDataDeferred = OrderApi.retrofitService.updateOrders(updatedOrders)
            val createOrdersDataDeferred = OrderApi.retrofitService.createOrders(newOrders)
            try{
                updateOrdersDataDeferred.await()
                createOrdersDataDeferred.await()
                _orderSuccessful.value = true
            } catch (t:Throwable){
                println(t.message)
                _toastMessage.value = "Out of stock"
            }

            // Refresh the screen
            fetchOrderData()
        }
    }


    private fun createNewOrder(preOrder: PreOrderItemsModel): OrderCreateRequest {
        return OrderCreateRequest(
            buyerUserId = sessionData.userId,
            itemAvailabilityId = preOrder.itemAvailabilityId,
            orderDescription = "Successfully created new order",
            deliveryLocation = sessionData.address,
            orderedQuantity = preOrder.orderedQuantity,
            orderedAmount = calculateOrderAmount(preOrder.orderedQuantity),
            discountAmount = 0.0,
            orderStatus = "ORDERED",
            paymentStatus = "",
            createdBy = "BUYER-" + sessionData.userName,
            updatedBy = "BUYER-" + sessionData.userName
        )
    }


    private fun createUpdateOrder(preOrder: PreOrderItemsModel): OrderUpdateRequest {

        return OrderUpdateRequest(
            orderId = preOrder.orderId,
            orderStatus = preOrder.orderStatus,
            discountAmount = null,
            orderedAmount = calculateOrderAmount(preOrder.orderedQuantity),
            orderedQuantity = preOrder.orderedQuantity,
            paymentStatus = null
        )
    }

    private fun calculateOrderAmount(orderedQuantity: Double): Double {
        return orderedQuantity.times(selectedItem.pricePerUnit ?: 0.0)
    }

}