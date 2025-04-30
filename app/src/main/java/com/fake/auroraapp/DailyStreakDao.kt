package com.fake.auroraapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface DailyStreakDao {

    @Query("SELECT * FROM DailyStreak WHERE userId = :userId")
    suspend fun getStreak(userId: Int): DailyStreak?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: DailyStreak)
}