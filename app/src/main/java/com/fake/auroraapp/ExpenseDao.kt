package com.fake.auroraapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Query("SELECT * FROM Expense WHERE categoryId = :categoryId")
    fun getExpenseByCategory(categoryId: Int): LiveData<List<Expense>>

    @Query("SELECT * FROM Expense")
    fun getAllExpense(): LiveData<List<Expense>>

    //return all the expenses for the user with this ID
    @Query("SELECT SUM(amount) FROM Expense WHERE userId = :userId")
    suspend fun getTotalExpenses(userId: Int): Double?

    @Delete
    suspend fun deleteExpense(expense: Expense)
}