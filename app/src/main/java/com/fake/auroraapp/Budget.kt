package com.fake.auroraapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Budget(
    @PrimaryKey val userId: Int,
    val monthlyBudget: Double,
    val amountLeft: Double,
    val minimumBudget: Double
)
