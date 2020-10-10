package com.f2h.f2h_buyer.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.*
import com.f2h.f2h_buyer.network.UserApi
import com.f2h.f2h_buyer.network.models.UserCreateRequest
import com.f2h.f2h_buyer.screens.MainActivity
import com.f2h.f2h_buyer.screens.UserPagesActivity
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

        var body = ""
        var title = ""
        if (remoteMessage.data.isNotEmpty()){
            val extras = Bundle()
            for ((key, value) in remoteMessage.data) {
                extras.putString(key, value)
            }
            if(extras.containsKey("body") && !extras.getString("body").isNullOrBlank()) {
                body = extras.getString("body")!!
            }
            if(extras.containsKey("title") && !extras.getString("title").isNullOrBlank()) {
                title = extras.getString("title")!!
            }
            saveNotification(title, body)
            sendNotification(title, body)
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


    private fun sendNotification(title: String, body: String){
        createNotificationChannel()
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        var builder = NotificationCompat.Builder(this, "100")
            .setSmallIcon(R.drawable.main_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("100", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}