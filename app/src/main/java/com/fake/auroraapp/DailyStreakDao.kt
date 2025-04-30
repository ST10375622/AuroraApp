package com.fake.auroraapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface DailyStreakDao {

    //Returns the streak for the user
    //Automattically updates via live data
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM DailyStreak WHERE userId = :userId")
    suspend fun getStreak(userId: Int): DailyStreak?

    //inserts and updates the streak
    //Code Attribution
    //Insert
    //Android Developers(2024)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: DailyStreak)
}