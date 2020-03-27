package com.f2h.f2h_buyer.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.LoginApi
import com.f2h.f2h_buyer.network.models.User
import kotlinx.coroutines.*
import retrofit2.await


class LoginViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    val loginPassword = MutableLiveData<String>()
    val loginMobile = MutableLiveData<String>()

    private val _loginResponse = MutableLiveData<User>()
    val loginResponse: LiveData<User>
        get() = _loginResponse

    private val _isLoginComplete = MutableLiveData<Boolean>()
    val isLoginComplete: LiveData<Boolean>
        get() = _isLoginComplete

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        _isLoginComplete.value = false
        fetchSavedSession()
    }

    fun onClickLoginButton() {
        val mobile: String = loginMobile.value.toString()
        val password: String = loginPassword.value.toString()
        tryToLogin(mobile, password)
    }

    private suspend fun saveSession() {
        return withContext(Dispatchers.IO) {
            val session = SessionEntity()
            database.clearSessions()
            session.address = _loginResponse.value?.address ?: ""
            session.email = _loginResponse.value?.email ?: ""
            session.userId = _loginResponse.value?.userId ?: 0L
            session.mobile = _loginResponse.value?.mobile ?: ""
            session.userName = _loginResponse.value?.userName ?: ""
            session.password = _loginResponse.value?.password ?: ""
            database.insert(session)
        }
    }

    private suspend fun retrieveSession() : SessionEntity {
        return withContext(Dispatchers.IO) {
            val sessions = database.getAll()
            var session = SessionEntity()
            if (sessions != null && sessions.size==1) {
                session = sessions[0]
                println(session.toString())
            } else {
                database.clearSessions()
            }
            return@withContext session
        }
    }

    private fun fetchSavedSession() {
        coroutineScope.launch {
            val session = retrieveSession()
            if (session != null && session.id != null) {
                tryToLogin(session.mobile, session.password)
            }
        }
    }

    private fun tryToLogin (mobile: String, password: String){
        coroutineScope.launch {
            var getUserDataDeferred = LoginApi.retrofitService.tryUserLogin(mobile, password)
            try {
                var userData = getUserDataDeferred.await()
                _loginResponse.value = userData
                println("Successfully logged in : "+ userData.toString())
                saveSession()
                _isLoginComplete.value = true
            } catch (t:Throwable){
                println(t.message)
                _loginResponse.value = null
                _isLoginComplete.value = true
            }
        }
        _isLoginComplete.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}