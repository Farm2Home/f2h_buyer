package com.f2h.f2h_buyer.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.User
import kotlinx.coroutines.*

class SettingsViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _response = MutableLiveData<User>()
    val response: LiveData<User>
        get() = _response

    private var userSession = SessionEntity()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getProfileInformation()
    }

    private fun getProfileInformation() {
        coroutineScope.launch {
            userSession = retrieveSession()
            var getUserDataDeferred = UserApi.retrofitService.getUserDetails(userSession.userId)
            try {
                var userData = getUserDataDeferred.await()
                _response.value = userData;
            } catch (t:Throwable){
                println(t.message)
            }
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}