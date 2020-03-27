package com.f2h.f2h_buyer.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.User
import kotlinx.coroutines.*

class SettingsViewModel : ViewModel() {

    private val _response = MutableLiveData<User>()
    val response: LiveData<User>
        get() = _response

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getProfileSettingsInformation()
    }

    private fun getProfileSettingsInformation() {
        coroutineScope.launch {
            var getUserDataDeferred = UserApi.retrofitService.getUserDetails(1)
            try {
                var userData = getUserDataDeferred.await()
                _response.value = userData;
            } catch (t:Throwable){
                println(t.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}