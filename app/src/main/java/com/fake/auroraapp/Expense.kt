package com.fake.auroraapp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(
    entity = Category:: class,
    parentColumns = ["id"],
    childColumns = ["categoryId"],
    onDelete = ForeignKey.CASCADE
)],
    indices = [Index("categoryId")]
    )

data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val userId: Int,
    val name: String,
    val description: String,
    val amount: Double,
    val date: String,
    val receiptUri: String? = null
)
