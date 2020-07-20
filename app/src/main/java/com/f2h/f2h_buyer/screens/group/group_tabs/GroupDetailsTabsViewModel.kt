package com.f2h.f2h_buyer.screens.group.group_tabs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.*
import kotlinx.coroutines.*
import com.f2h.f2h_buyer.constants.F2HConstants.USER_ROLE_BUYER
import com.f2h.f2h_buyer.network.models.GroupMembershipRequest

class GroupDetailsTabsViewModel (val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {
    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private val sessionData = MutableLiveData<SessionEntity>()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        _isProgressBarActive.value = false
    }


    fun onClickExitGroup() {
        _isProgressBarActive.value = true
        coroutineScope.launch {
            sessionData.value = retrieveSession()
            var membershipId: Long = -1L
            var role: String = ""

            var getGroupMembershipsDeferred = GroupMembershipApi.retrofitService.getGroupMembership(sessionData.value!!.groupId, sessionData.value!!.userId)
            try {
                var memberships = getGroupMembershipsDeferred.await()
                println(memberships)
                role = memberships[0].roles!!
                membershipId = memberships[0].groupMembershipId!!
            } catch (t:Throwable){
                println(t.message)
                _isProgressBarActive.value = false
                return@launch
            }

            var roleList = role.split(",")
            role = roleList.minus(USER_ROLE_BUYER).joinToString()


            if (role.isNullOrBlank()){
                coroutineScope.launch {
                    var deleteGroupMembershipDataDeferred =
                        GroupMembershipApi.retrofitService.deleteGroupMembership(membershipId)
                    try {
                        var deleteMembership = deleteGroupMembershipDataDeferred.await()
                    } catch (t:Throwable){
                        println(t.message)

                    }
                }
            }
            else{
                println(role)
                var membershipRequest = GroupMembershipRequest(
                    null,
                    null,
                    role,
                    null
                )
                coroutineScope.launch {
                    var updateGroupMembershipDataDeferred =
                        GroupMembershipApi.retrofitService.updateGroupMembership(membershipId, membershipRequest)
                    try {
                        var updatedMembership = updateGroupMembershipDataDeferred.await()
                    } catch (t:Throwable){
                        println(t.message)
                    }
                }
            }

            _isProgressBarActive.value = false
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



}