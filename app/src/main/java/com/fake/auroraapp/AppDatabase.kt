package com.fake.auroraapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [User::class, Budget::class, Category::class, Expense::class, Notification::class, TreeProgress::class, DailyStreak::class],
    version = 4,
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
        val MIGRATION_3_4 = object : Migration(3, 4) {
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