package com.fake.auroraapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    //Lists all the tables that within the database
    entities = [User::class, Budget::class, Category::class, Expense::class, Notification::class, TreeProgress::class, DailyStreak::class],
    version = 4,// this is schema version, it is on number 4 as some changes were made to the database
    exportSchema =  false
)

abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun notificationDao(): NotificationDao
    abstract fun TreeProgressDao(): TreeProgressDao
    abstract fun DailyStreakDao(): DailyStreakDao

    companion object{
        @Volatile private var INSTANCE: AppDatabase? = null

        //made a change to the version of my database so i had to make a migration
        //Code Attribution
        //Migrate Room Database
        //Android Developers(2024)
        val MIGRATION_3_4 = object : Migration(3, 4) { //this is the migration from the old version to the new version
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
             CREATE TABLE IF NOT EXISTS `DailyStreak` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `userId` INTEGER NOT NULL,
                `currentStreak` INTEGER NOT NULL,
                `lastLoggedDate` TEXT NOT NULL
            )
        """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}