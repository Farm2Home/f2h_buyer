package com.f2h.f2h_buyer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SessionEntity::class, NotificationEntity::class], version = 4, exportSchema = false)
abstract class F2HDatabase : RoomDatabase() {

    abstract val sessionDatabaseDao: SessionDatabaseDao
    abstract val notificationDatabaseDao: NotificationDatabaseDao

    companion object{

        @Volatile
        private var INSTANCE: F2HDatabase? = null

        fun getInstance(context: Context) : F2HDatabase {
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        F2HDatabase::class.java,
                        "f2h_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}