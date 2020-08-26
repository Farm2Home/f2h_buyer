package com.f2h.f2h_buyer.utils

import android.os.Bundle
import android.util.Log
import com.f2h.f2h_buyer.database.*
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.UserCreateRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "Service"
    private val sessionDatabase: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val notificationDatabase: NotificationDatabaseDao by lazy { F2HDatabase.getInstance(application).notificationDatabaseDao }
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.i(TAG, "From: " + remoteMessage.from)
        Log.i(TAG, "Notification Message Body: " + remoteMessage.notification?.body!!)


        saveNotification(remoteMessage.notification?.title!!, remoteMessage.notification?.body!!)

        if (remoteMessage.data.isNotEmpty()){
            val extras = Bundle()
            for ((key, value) in remoteMessage.data) {
                extras.putString(key, value)
            }
            if(extras.containsKey("message") && !extras.getString("message").isNullOrBlank()) {
//                saveNotification(extras.getString("message")!!)
            }
        }

    }


    private fun saveNotification(messageTitle:String, messageBody: String) {

        coroutineScope.launch {

            var notification = NotificationEntity(title = messageTitle, body = messageBody)
            saveNotificationInDB(notification)
        }
    }


    override fun onNewToken(token: String) {
        Log.i(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }


    fun sendRegistrationToServer(token: String){
        //Make API call and update user with new token
        coroutineScope.launch {
            var userSession = retrieveSession()
            if (userSession.userId > 0) {
                Log.i(TAG, "userSession" + userSession)
                var updateTokenPayload = tokenUpdatePayload(token)
                var updateUserData =
                    UserApi.retrofitService.updateUser(userSession.userId, updateTokenPayload)
                updateUserData.await()
            }
        }
    }

    private fun tokenUpdatePayload(token: String): UserCreateRequest {
        var updatedUserPayload = UserCreateRequest (
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            token
        )
        return updatedUserPayload
    }


    private suspend fun retrieveSession() : SessionEntity {
        return withContext(Dispatchers.IO) {
            val sessions = sessionDatabase.getAll()
            var session = SessionEntity()
            if (sessions.size==1) {
                session = sessions[0]
                println(session.toString())
            } else {
                sessionDatabase.clearSessions()
            }
            return@withContext session
        }
    }

    private suspend fun saveNotificationInDB(notificationEntity: NotificationEntity)   {
        return withContext(Dispatchers.IO) {
            notificationDatabase.insert(notificationEntity)
            notificationDatabase.removeOldNotifications()
        }
    }


}