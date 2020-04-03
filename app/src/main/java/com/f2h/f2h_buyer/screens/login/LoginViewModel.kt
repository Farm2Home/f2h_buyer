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
        var session = SessionEntity(mobile = mobile, password = password )
        tryToLogin(session)
    }

    private suspend fun saveSession(updatedUserData: User, preSavedSession: SessionEntity) {
        return withContext(Dispatchers.IO) {
            database.clearSessions()
            preSavedSession.address = updatedUserData.address
            preSavedSession.email = updatedUserData.email
            preSavedSession.userId = updatedUserData.userId
            preSavedSession.mobile = updatedUserData.mobile
            preSavedSession.userName = updatedUserData.userName
            preSavedSession.password = updatedUserData.password
            database.insert(preSavedSession)
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
                loginPassword.value = session.password
                loginMobile.value = session.mobile
                tryToLogin(session)
            }
        }
    }

    private fun tryToLogin (session: SessionEntity){
        coroutineScope.launch {
            var getUserDataDeferred = LoginApi.retrofitService.tryUserLogin(session.mobile, session.password)
            try {
                var updatedUserData = getUserDataDeferred.await()
                if (updatedUserData != null){
                    _loginResponse.value = updatedUserData
                    saveSession(updatedUserData, session)
                    println("Successfully logged in : "+ updatedUserData.toString())
                }
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