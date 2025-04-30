package com.fake.auroraapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TreeProgressDao {
    //fetches the treeProgress record that is associated to that specific user and ensures that only
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM TreeProgress WHERE userId = :userId LIMIT 1")
    suspend fun getTreeProgress(userId: Int): TreeProgress?

    //inserts a new tree progress
    //Code Attribution
    //Insert
    //Android Developers(2024)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(treeProgress: TreeProgress)

    //fetches the treeProgress recors for a given user and sorts from newest to oldest
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM TreeProgress WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllProgressForUser(userId: Int): List<TreeProgress>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(treeProgress: TreeProgress)


}