package com.fake.auroraapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.Month
import java.time.Year

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

    @Query("SELECT * FROM Expense WHERE strftime('%m', date) = :month and strftime('%Y', date) = :year")
    fun getExpensesByMonth(month: String, year: String): LiveData<List<Expense>>

    @Query("""
        /*gets the category name*/
        SELECT c.name AS category, SUM(e.amount) AS total
    FROM expense e
    /*joins each category with its expense*/
    INNER JOIN category c ON e.categoryId = c.id
    WHERE strftime('%m', e.date) = :month AND strftime('%Y', e.date) = :year
    /*grouped by category name*/
    GROUP BY c.name
    ORDER BY total DESC
    LIMIT 1
        """)
    suspend fun getTopCategory(month: String, year: String): TopCategory?

    @Query("SELECT SUM(amount) FROM expense WHERE strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    suspend fun getTotalSpent(month: String, year: String): Double?

    @Query("SELECT COUNT(*) FROM expense WHERE strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    suspend fun getTransactionCount(month: String, year: String): Int

    @Query("SELECT * FROM Expense WHERE strftime('%m', date) = :month and strftime('%Y', date) = :year")
    suspend fun getExpensesByMonthNow(month: String, year: String): List<Expense>

    @Query("SELECT COUNT(*) FROM Expense WHERE userId = :userId")
    suspend fun getTransactionCount(userId: Int): Int

}