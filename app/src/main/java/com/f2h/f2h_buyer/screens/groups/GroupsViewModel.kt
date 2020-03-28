package com.f2h.f2h_buyer.screens.groups

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.GroupApi
import com.f2h.f2h_buyer.network.models.Group
import kotlinx.coroutines.*

class GroupsViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _groups = MutableLiveData<List<Group>>()

    val group: LiveData<List<Group>>
        get() = _groups


    private val roles = listOf<String>("Buyer","Group_Admin")
    private var userSession = SessionEntity()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getUserGroupsInformation()
    }

    fun updateSelectedGroup(groupId: Long){
        coroutineScope.launch {
            updateSessionActiveGroupId(groupId)
        }
    }

    private fun getUserGroupsInformation() {
        coroutineScope.launch {
            userSession = retrieveSession()
            var getGroupsDataDeferred = GroupApi.retrofitService.getUserGroups(userSession.userId, roles)
            try {
                var userGroups = getGroupsDataDeferred.await()
                if (userGroups != null && userGroups.size > 0) {
                    _groups.value = userGroups
                }
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

    private suspend fun updateSessionActiveGroupId(groupId: Long){
        return withContext(Dispatchers.IO) {
            database.updateActiveGroup(groupId)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}