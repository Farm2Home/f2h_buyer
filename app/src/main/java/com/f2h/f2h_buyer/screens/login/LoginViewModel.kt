package com.f2h.f2h_buyer.screens.login

import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.LoginApi
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.User
import com.f2h.f2h_buyer.network.models.UserCreateRequest
import kotlinx.coroutines.*
import retrofit2.await
import java.nio.charset.Charset


class LoginViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    val loginPassword = MutableLiveData<String>()
    val loginMobile = MutableLiveData<String>()
    val fcmToken = MutableLiveData<String>()

    private val _loginResponse = MutableLiveData<User>()
    val loginResponse: LiveData<User>
        get() = _loginResponse

    private val _isLoginComplete = MutableLiveData<Boolean>()
    val isLoginComplete: LiveData<Boolean>
        get() = _isLoginComplete

    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        _isLoginComplete.value = false
        _isProgressBarActive.value = true
        fetchSavedSession()
    }

    fun onClickLoginButton() {
        _isProgressBarActive.value = true
        val mobile: String = loginMobile.value.toString()
        val password: String = Base64.encodeToString(loginPassword.value.toString().toByteArray(), Base64.DEFAULT)
        var session = SessionEntity(mobile = mobile, password = password )
        tryToLogin(session)
    }


    private suspend fun saveSession(updatedUserData: User, preSavedSession: SessionEntity) {
        return withContext(Dispatchers.IO) {
            database.clearSessions()
            preSavedSession.address = updatedUserData.address ?: ""
            preSavedSession.email = updatedUserData.email ?: ""
            preSavedSession.userId = updatedUserData.userId ?: -1L
            preSavedSession.mobile = updatedUserData.mobile ?: ""
            preSavedSession.userName = updatedUserData.userName ?: ""
            preSavedSession.password = updatedUserData.password ?: ""
            database.insert(preSavedSession)
        }
    }

    private suspend fun retrieveSession() : SessionEntity {
        return withContext(Dispatchers.IO) {
            val sessions = database.getAll()
            var session = SessionEntity()
            if (sessions.size==1) {
                session = sessions[0]
                println(session.toString())
            } else {
                database.clearSessions()
            }
            return@withContext session
        }
    }

    private fun fetchSavedSession(): SessionEntity {
        var session = SessionEntity()
        coroutineScope.launch {
            session = retrieveSession()
            loginPassword.value = String(Base64.decode(session.password, Base64.DEFAULT), Charset.defaultCharset())
            loginMobile.value = session.mobile
            if (session.id != 0L){
                tryToLogin(session)
            } else{
                _isLoginComplete.value = false
                _isProgressBarActive.value = false
            }
        }
        return session
    }

    private fun tryToLogin (session: SessionEntity){
        coroutineScope.launch {
            var getUserDataDeferred = LoginApi.retrofitService.tryUserLogin(session.mobile, session.password)
            try {
                var updatedUserData = getUserDataDeferred.await()
                _loginResponse.value = updatedUserData

                if (updatedUserData.buyerFcmToken.isNullOrEmpty() || session.userId <= 0) {
                    var updateTokenPayload = tokenUpdatePayload()
                    var updateUserData =
                        UserApi.retrofitService.updateUser(updatedUserData.userId!!, updateTokenPayload)
                    updateUserData.await()
                }

                saveSession(updatedUserData, session)
                _isLoginComplete.value = true
            } catch (t:Throwable){
                println(t.message)
                _loginResponse.value = null
                _isLoginComplete.value = true
            }
            _isProgressBarActive.value = false
        }
    }


    private fun tokenUpdatePayload(): UserCreateRequest {
        var updatedUserPayload = UserCreateRequest (
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            fcmToken.value
        )
        return updatedUserPayload
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}