package com.fake.auroraapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {

    @Insert
    suspend fun insertNotification(notification: Notification)

    @Query("SELECT * FROM Notification ORDER BY date DESC")
    fun getAllNotifications(): LiveData<List<Notification>>


}