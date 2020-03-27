package com.f2h.f2h_buyer.screens.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GroupsViewModel : ViewModel() {


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
            var getUserDataDeferred = UserApi.retrofitService.getUserDetails(2)
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