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

    //Returns all the Budget
    //Live data keeps the User Interface updated
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM Budget WHERE UserId = :userId")
    fun getBudget(userId: Int): LiveData<Budget>

    //Returns all the budget value
    //Live data keeps the User Interface updated
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM Budget WHERE userId = :userId")
    suspend fun getBudgetValue(userId: Int): Budget?
}