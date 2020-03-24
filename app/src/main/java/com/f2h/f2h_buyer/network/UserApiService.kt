package com.f2h.f2h_buyer.network

import com.f2h.f2h_buyer.network.models.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "http://f2h.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface UserApiService{
    @GET("user/{user_id}")
    fun getUserDetails(@Path("user_id") userId: Long):
            Call<User>
}

object UserApi {
    val retrofitService : UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}