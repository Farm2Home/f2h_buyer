package com.f2h.f2h_buyer.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsViewModel : ViewModel() {

    private val _response = MutableLiveData<User>()

    val response: LiveData<User>
        get() = _response

    init {
        getProfileSettingsInformation()
    }

    private fun getProfileSettingsInformation() {
        UserApi.retrofitService.getUserDetails(1).enqueue(object: Callback<User>{
            override fun onFailure(call: Call<User>, t: Throwable) {
                println(t.message)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                _response.value = response.body()
            }
        })
    }
}