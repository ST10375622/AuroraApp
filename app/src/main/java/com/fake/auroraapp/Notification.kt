package com.fake.auroraapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notification")
data class Notification (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val date: String
)