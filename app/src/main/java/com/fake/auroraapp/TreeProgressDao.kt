package com.fake.auroraapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TreeProgressDao {
    @Query("SELECT * FROM TreeProgress WHERE userId = :userId LIMIT 1")
    suspend fun getTreeProgress(userId: Int): TreeProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(treeProgress: TreeProgress)

    @Query("SELECT * FROM TreeProgress WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllProgressForUser(userId: Int): List<TreeProgress>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(treeProgress: TreeProgress)


}