package com.fake.auroraapp

import androidx.lifecycle.LiveData

class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun insertNotification(notification: Notification){
        notificationDao.insertNotification(notification)
    }

    fun getAllNotification(): LiveData<List<Notification>> {
        return notificationDao.getAllNotifications()
    }
}