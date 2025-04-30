package com.fake.auroraapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.*

@Dao
interface UserDao {
    //inserts a new user, replace if conflict occurs
    //Code Attribution
    //Insert
    //Android Developers(2024)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    //gets the user by email
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    //this is a login query
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    //
    //Code Attribution
    //Query
    //Android Developers(2024)
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?
}