package com.fake.auroraapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    //Returns categories for specific user
    //Live data keeps the User Interface updated
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM Category WHERE userId = :userId")
    fun getCategoriesByUser(userId: Int): LiveData<List<Category>>

    //Returns category name
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT name FROM Category WHERE id = :categoryId")
    suspend fun getCategoryNameById(categoryId: Int): String?

    //Deletes a category if required
    //Code Attribution
    //Delete
    //Android Developers(2024)
    @Delete
    suspend fun deleteCategory(category: Category)
}