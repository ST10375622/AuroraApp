package com.fake.auroraapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int =0,
    val userId: Int,
    val name:String
)
