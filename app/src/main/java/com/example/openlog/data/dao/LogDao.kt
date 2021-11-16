package com.example.openlog.data.dao

import androidx.room.*
import com.example.openlog.data.entity.Log
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: Log)

    @Update
    suspend fun update(log: Log)

    @Delete
    suspend fun delete(log: Log)

    @Query("SELECT * from log WHERE log_id = :id")
    fun getLog(id: Int): Flow<Log>

    @Query("SELECT * from log ORDER BY log_date ASC")
    fun getLogs(): Flow<List<Log>>
}