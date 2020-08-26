package com.f2h.f2h_buyer.database


import androidx.room.*

@Dao
interface NotificationDatabaseDao {

    @Insert
    fun insert(notificationEntity: NotificationEntity)

    @Update
    fun update(notificationEntity: NotificationEntity)

    @Query("SELECT * FROM notification_table WHERE id = :id")
    fun get(id: Long): NotificationEntity

    @Query("SELECT * FROM notification_table order by id desc")
    fun getAll(): List<NotificationEntity>

    @Query("SELECT count(*) FROM notification_table where is_read = 0")
    fun getUnreadCount(): Int

    @Query("DELETE FROM notification_table where id not in (select id from notification_table order by id desc limit 10)")
    fun removeOldNotifications()

    @Query("DELETE FROM notification_table")
    fun removeAll()

}