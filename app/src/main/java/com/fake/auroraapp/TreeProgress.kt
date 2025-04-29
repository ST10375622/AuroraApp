package com.fake.auroraapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TreeProgress (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val progress: Int,
    val timestamp: String
)
