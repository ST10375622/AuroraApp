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

    //Returns all the expenses for a specific category.
    //Automattically updates via live data
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM Expense WHERE categoryId = :categoryId")
    fun getExpenseByCategory(categoryId: Int): LiveData<List<Expense>>

    //Returns all the expenses
    //Live data keeps the User Interface updated
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM Expense")
    fun getAllExpense(): LiveData<List<Expense>>

    //Returns the total expense amount for a specific user
    //Code Attribution
    //Query
    //Android Developers(2024)
    //return all the expenses for the user with this ID
    @Query("SELECT SUM(amount) FROM Expense WHERE userId = :userId")
    suspend fun getTotalExpenses(userId: Int): Double?

    //Deletes any expense that needs to be deleted
    //Code Attribution
    //Delete
    //Android Developers(2024)
    @Delete
    suspend fun deleteExpense(expense: Expense)

    //returns expenses for a given month and year
    //makes use of liveData
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM Expense WHERE strftime('%m', date) = :month and strftime('%Y', date) = :year")
    fun getExpensesByMonth(month: String, year: String): LiveData<List<Expense>>

    //returns the category with the highest spending for that given month
    //Code Attribution
    //Query
    //Android Developers(2024)
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

    //returns the total amount spent for specific month
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT SUM(amount) FROM expense WHERE strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    suspend fun getTotalSpent(month: String, year: String): Double?

    //Returns the number of transactions for a given month
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT COUNT(*) FROM expense WHERE strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    suspend fun getTransactionCount(month: String, year: String): Int

    @Query("SELECT * FROM Expense WHERE strftime('%m', date) = :month and strftime('%Y', date) = :year")
    suspend fun getExpensesByMonthNow(month: String, year: String): List<Expense>

    //Returns total number of transactions for a specific user
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT COUNT(*) FROM Expense WHERE userId = :userId")
    suspend fun getTransactionCount(userId: Int): Int

}