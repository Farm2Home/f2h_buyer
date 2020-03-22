package com.f2h.f2h_buyer.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://f2h.herokuapp.com/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface LoginApiService{
    @GET("user/login")
    fun getUserDetails(@Query("mobile") mobile: String, @Query("password") password: String):
            Call<String>
}

object LoginApi {
    val retrofitService : LoginApiService by lazy {
        retrofit.create(LoginApiService::class.java)
    }
}