package com.fake.auroraapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: Budget)

    @Query("SELECT * FROM Budget WHERE UserId = :userId")
    fun getBudget(userId: Int): LiveData<Budget>
}