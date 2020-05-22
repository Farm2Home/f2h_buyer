package com.f2h.f2h_buyer.screens.search_group

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.constants.F2HConstants.USER_ROLE_BUYER
import com.f2h.f2h_buyer.constants.F2HConstants.USER_ROLE_BUYER_REQUESTED
import com.f2h.f2h_buyer.constants.F2HConstants.USER_ROLE_GROUP_ADMIN
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.GroupApi
import com.f2h.f2h_buyer.network.models.Group
import kotlinx.coroutines.*

class SearchGroupsViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _groups = MutableLiveData<List<SearchGroupsItemsModel>>()
    val group: LiveData<List<SearchGroupsItemsModel>>
        get() = _groups

    private val _localities = MutableLiveData<List<String>>()
    val localities: LiveData<List<String>>
        get() = _localities

    private val _selectedLocality = MutableLiveData<List<String>>()
    val selectedLocality: LiveData<List<String>>
        get() = _selectedLocality


    private val roles = listOf<String>(USER_ROLE_BUYER, USER_ROLE_GROUP_ADMIN, USER_ROLE_BUYER_REQUESTED)
    private var userGroups = listOf<Group>()
    private var userSession = SessionEntity()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        fetchAllLocalities()
        getUserGroupsInformation()
    }


    private fun getUserGroupsInformation() {
        coroutineScope.launch {
            userSession = retrieveSession()
            var getMemberGroupsDataDeferred = GroupApi.retrofitService.getUserGroups(userSession.userId, roles)
            try {
                userGroups = getMemberGroupsDataDeferred.await()
                getGroupsInfoForLocalities()
            } catch (t:Throwable){
                println(t.message)
            }
        }
    }

    private fun getGroupsInfoForLocalities() {
        coroutineScope.launch {
            userSession = retrieveSession()
            var getSearchGroupsDataDeferred = GroupApi.retrofitService.searchGroupsByLocality(_selectedLocality.value ?: listOf())
            try {
                var searchedGroups = getSearchGroupsDataDeferred.await()
                _groups.value = createSearchGroupsItemList(userGroups, searchedGroups)
            } catch (t:Throwable){
                println(t.message)
            }
        }
    }

    private fun createSearchGroupsItemList(userGroups: List<Group>, searchedGroups: List<Group>): List<SearchGroupsItemsModel>? {
        var searchGroupsItemList = arrayListOf<SearchGroupsItemsModel>()
        searchedGroups.forEach { group ->
            var uiElement = SearchGroupsItemsModel(
                group.groupId ?: -1,
                group.ownerUserId ?: -1,
                group.groupName ?: "",
                group.description ?: "",
                false
            )

            userGroups.forEach{ userGroup ->
                if (uiElement.groupId.equals(userGroup.groupId)) {
                    uiElement.isAlreadyMember = true
                }
            }
            searchGroupsItemList.add(uiElement)
        }

        return searchGroupsItemList
    }


    private fun fetchAllLocalities(){
//        coroutineScope.launch {
//            userSession = retrieveSession()
//            var getLocalitiesDataDeferred = GroupApi.retrofitService.searchGroupsByLocality(_selectedLocality.value ?: listOf())
//            try {
//                var searchedGroups = getSearchGroupsDataDeferred.await()
//                _groups.value = createSearchGroupsItemList(userGroups, searchedGroups)
//            } catch (t:Throwable){
//                println(t.message)
//            }
//        }
        _localities.value = listOf("ERNAKULAM","KOCHI", "CALICUT")
    }


    fun onLocalitiesSelected(position: Int) {
        _selectedLocality.value = arrayListOf(localities.value?.get(position) ?: "")
        getGroupsInfoForLocalities()
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


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}