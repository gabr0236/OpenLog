package com.example.openlog.data

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.openlog.data.dao.LogCategoryDao
import com.example.openlog.data.dao.LogItemDao
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class LogItemDatabaseTest {
    private lateinit var logItemDao: LogItemDao
    private lateinit var logCategoryDao: LogCategoryDao
    private lateinit var db: LogItemDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LogItemDatabase::class.java).build()
        logItemDao = db.logItemDao()
        logCategoryDao = db.logCategoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadLogCategory() = runBlocking {
        val logCategory: LogCategory = LogCategory(
            name = "Water",
            unit = "L",
            emojiId = 0
        );
        logCategoryDao.insert(logCategory)
        val byName = logCategoryDao.getLogCategory("Water").first()
        MatcherAssert.assertThat(byName, CoreMatchers.equalTo(logCategory))
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadLogItem() = runBlocking {
        val logItem: LogItem = LogItem(
            id = 100,
            categoryOwnerName = "Water",
            value = 1.2f,
            date = Date()
        );
        logItemDao.insert(logItem)
        val byId = logItemDao.getLogItem(100).first()
        MatcherAssert.assertThat(byId, CoreMatchers.equalTo(logItem))
    }
}