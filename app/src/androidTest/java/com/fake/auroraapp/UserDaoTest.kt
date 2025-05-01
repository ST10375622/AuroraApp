package com.fake.auroraapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // okay in tests only
            .build()
        userDao = db.userDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertUser_and_retrieveByEmail() = runBlocking {
        val testUser = User(
            email = "test@email.com",
            name = "Test",
            dob = "2000-02-02",
            password = "Password2.0"
        )
        val userId = userDao.insertUser(testUser)
        val retrievedUser = userDao.getUserByEmail("test@email.com")

        assertNotNull(retrievedUser)
        assertEquals(testUser.name, retrievedUser?.name)
        assertEquals(testUser.email, retrievedUser?.email)
        assertEquals(testUser.password, retrievedUser?.password)
        assertEquals(testUser.dob, retrievedUser?.dob)
    }

    @Test
    fun login_returnsCorrectUser() = runBlocking {
        val user = User(
            email = "test2@email.com",
            name = "Test2",
            dob = "2002-02-02",
            password = "Password2.0"
        )
        userDao.insertUser(user)

        val loginUser = userDao.login("test2@email.com", "Password2.0")

        assertNotNull(loginUser)
        assertEquals("Login User", loginUser?.name)
    }

}