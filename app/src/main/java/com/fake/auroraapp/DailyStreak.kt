package com.fake.auroraapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyStreak (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var userId: Int,
    var currentStreak: Int = 0,
    var lastLoggedDate: String = ""
)