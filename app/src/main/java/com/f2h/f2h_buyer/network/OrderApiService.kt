package com.f2h.f2h_buyer.network

import com.f2h.f2h_buyer.constants.F2HConstants.SERVER_URL
import com.f2h.f2h_buyer.network.models.Order
import com.f2h.f2h_buyer.network.models.OrderCreateRequest
import com.f2h.f2h_buyer.network.models.OrderHeader
import com.f2h.f2h_buyer.network.models.OrderUpdateRequest
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = SERVER_URL

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface OrderApiService {
    @GET("order_header")
    fun getOrderHeadersForGroupUserAndItem(@Query("group_id") groupId: Long, @Query("buyer_user_id") buyerUserId: Long,
                                     @Query("item_id") itemId: Long?, @Query("start_date") startDate: String?,
                                     @Query("end_date") endDate: String?): Deferred<List<OrderHeader>>

    @GET("order")
    fun getOrdersForGroupUserAndItem(@Query("group_id") groupId: Long, @Query("buyer_user_id") buyerUserId: Long,
                                     @Query("item_id") itemId: Long?, @Query("start_date") startDate: String?,
                                     @Query("end_date") endDate: String?): Deferred<List<Order>>

    @PUT("orders/update_all")
    fun updateOrders(@Body orderUpdateRequests: List<OrderUpdateRequest>): Deferred<List<Order>>

    @POST("orders/save_all")
    fun createOrders(@Body createOrders: List<OrderCreateRequest>): Deferred<List<Order>>

}

object OrderApi {
    val retrofitService : OrderApiService by lazy {
        retrofit.create(OrderApiService::class.java)
    }
}
