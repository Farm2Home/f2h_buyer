package com.f2h.f2h_buyer.database

import androidx.room.*

@Dao
interface SessionDatabaseDao {

    @Insert
    fun insert(sessionEntity: SessionEntity)

    @Update
    fun update(sessionEntity: SessionEntity)

    @Query("SELECT * FROM session_table WHERE id = :id")
    fun get(id: Long): SessionEntity

    @Query("SELECT * FROM session_table")
    fun getAll(): List<SessionEntity>

    @Query("DELETE FROM session_table")
    fun clearSessions()

}