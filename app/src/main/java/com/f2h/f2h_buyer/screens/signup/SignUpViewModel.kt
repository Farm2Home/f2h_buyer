package com.f2h.f2h_buyer.screens.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.User
import com.f2h.f2h_buyer.network.models.UserCreateRequest
import kotlinx.coroutines.*


class SignUpViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    val userName = MutableLiveData<String>()
    val mobile = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val email = MutableLiveData<String>()


    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private val _isSignUpComplete = MutableLiveData<Boolean>()
    val isSignUpComplete: LiveData<Boolean>
        get() = _isSignUpComplete

    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String>
        get() = _toastText

    private val signUpUiModel = SignUpUiModel()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
       _isProgressBarActive.value = false
        _isSignUpComplete.value = false
    }


    fun onCreateButtonClick() {
        if(isAnyFieldInvalid()){
            return
        }
        _isProgressBarActive.value = true
        var newUser = createUserRequestObject()
        coroutineScope.launch {
            val getCreatedUser = UserApi.retrofitService.createUser(newUser)
            try {
                var createdUser = getCreatedUser.await()
                if (createdUser != null){
                    saveSession(createdUser)
                }
                _isSignUpComplete.value = true
            } catch (t:Throwable){
                println(t.message)
                _toastText.value = "Oops, looks like the user already exists"
            }
            _isProgressBarActive.value = false
        }
    }


    fun isAnyFieldInvalid(): Boolean{
        if (userName.value.isNullOrBlank()) {
            _toastText.value = "Please enter a name"
            return true
        }
        if (address.value.isNullOrBlank()) {
            _toastText.value = "Please enter a delivery address"
            return true
        }
        if (mobile.value.isNullOrBlank()) {
            _toastText.value = "Please enter a mobile number"
            return true
        }
        if (email.value.isNullOrBlank()) {
            _toastText.value = "Please enter an email"
            return true
        }
        if (password.value.isNullOrBlank()) {
            _toastText.value = "Please enter a password"
            return true
        }

        return false
    }

    private fun createUserRequestObject() : UserCreateRequest{
        var userObject = UserCreateRequest()
        userObject.userName = userName.value
        userObject.address = address.value
        userObject.mobile = mobile.value
        userObject.email = email.value
        userObject.password = password.value
        userObject.createdBy = userName.value
        userObject.updatedBy = userName.value
        return userObject
    }

    private suspend fun saveSession(updatedUserData: User) {
        return withContext(Dispatchers.IO) {
            database.clearSessions()
            var savedSession = SessionEntity()
            savedSession.address = updatedUserData.address ?: ""
            savedSession.email = updatedUserData.email ?: ""
            savedSession.userId = updatedUserData.userId ?: -1L
            savedSession.mobile = updatedUserData.mobile ?: ""
            savedSession.userName = updatedUserData.userName ?: ""
            savedSession.password = updatedUserData.password ?: ""
            database.insert(savedSession)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}