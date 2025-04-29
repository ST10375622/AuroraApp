package com.fake.auroraapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationRepository = NotificationRepository(AppDatabase.getDatabase(application).notificationDao())

    fun getAllNotifications(): LiveData<List<Notification>> {
        return notificationRepository.getAllNotification()
    }
}