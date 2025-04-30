package com.fake.auroraapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {

    //inserts notifications
    //Code Attribution
    //Insert
    //Android Developers(2024)
    @Insert
    suspend fun insertNotification(notification: Notification)

    //fetches all the notifications and orders them by the date from oldest
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM Notification ORDER BY date DESC")
    fun getAllNotifications(): LiveData<List<Notification>>


}