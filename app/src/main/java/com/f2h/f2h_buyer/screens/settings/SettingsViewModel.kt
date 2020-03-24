package com.f2h.f2h_buyer.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _response = MutableLiveData<String>()

    val response: LiveData<String>
        get() = _response

    init {
        getProfileSettingsInformation()
    }

    private fun getProfileSettingsInformation() {
        _response.value = "Response from User Profile API goes here"
    }
}