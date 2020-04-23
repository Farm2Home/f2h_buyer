package com.f2h.f2h_buyer.network

import com.f2h.f2h_buyer.network.models.ItemAvailability
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://f2h.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


interface ItemAvailabilityApiService{
    @GET("item_availability")
    fun getItemAvailabilities(@Query("item_availability_ids") availabilityIds: List<Long>):
            Deferred<List<ItemAvailability>>
}


object ItemAvailabilityApi {
    val retrofitService : ItemAvailabilityApiService by lazy {
        retrofit.create(ItemAvailabilityApiService::class.java)
    }
}
