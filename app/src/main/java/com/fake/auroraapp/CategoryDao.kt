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

    @Query("SELECT * FROM Category WHERE userId = :userId")
    fun getCategoriesByUser(userId: Int): LiveData<List<Category>>

    @Query("SELECT name FROM Category WHERE id = :categoryId")
    suspend fun getCategoryNameById(categoryId: Int): String?


    @Delete
    suspend fun deleteCategory(category: Category)
}