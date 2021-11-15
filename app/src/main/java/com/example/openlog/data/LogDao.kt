package com.example.openlog.data

import androidx.room.*
import com.example.openlog.constants.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: Log)

    @Update
    suspend fun update(log: Log)

    @Delete
    suspend fun delete(log: Log)

    @Query("SELECT * from log WHERE id = :id")
    fun getLog(id: Int): Flow<Log>

    @Query("SELECT * from log ORDER BY date ASC")
    fun getLogs(): Flow<List<Log>>
}