package com.fake.auroraapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseDao = AppDatabase.getDatabase(application).expenseDao()
    private val db = AppDatabase.getDatabase(application)
    private val treeProgressDao = AppDatabase.getDatabase(application).TreeProgressDao()
    private val _dailyStreak = MutableLiveData<DailyStreak?>()
    val dailyStreak: LiveData<DailyStreak?> get() = _dailyStreak

    fun getTotalExpensesLive(userId: Int): LiveData<Double?> {
        return object : LiveData<Double?>() {
            override fun onActive() {
                super.onActive()
                viewModelScope.launch {
                    val total = expenseDao.getTotalExpenses(userId)
                    postValue(total)
                }
            }

        }
    }

    /*Code Attribution
   * View Model
   * Android Developers(2024)*/
    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.insertExpense(expense)

            val today = LocalDate.now()
            val todayStr =today.toString()

            val streakDao = db.DailyStreakDao()
            val existingStreak = streakDao.getStreak(expense.userId)


            if (existingStreak != null) {
                val lastLoggedDate = LocalDate.parse(existingStreak.lastLoggedDate)

                if (lastLoggedDate != today) {
                    val daysBetween = ChronoUnit.DAYS.between(lastLoggedDate, today)

                    when {
                        daysBetween == 1L -> {
                            // Consecutive day - increment streak
                            existingStreak.currentStreak += 1
                        }

                        daysBetween > 1L -> {
                            // Missed a day - reset streak
                            existingStreak.currentStreak = 1
                        }
                    }

                    existingStreak.lastLoggedDate = todayStr
                    streakDao.insertOrUpdateStreak(existingStreak)
                }
                _dailyStreak.postValue(existingStreak)
            } else {
                //First Time Logging - starting new streak
                val newStreak = DailyStreak(
                    userId = expense.userId,
                    currentStreak = 1,
                    lastLoggedDate = todayStr
                )
                streakDao.insertOrUpdateStreak(newStreak)
                _dailyStreak.postValue(newStreak)
            }
        }
    }

    /*Code Attribution
   * View Model Scope
   * Android Developers(2024)*/
    fun loadStreak(userId: Int) {
        viewModelScope.launch{
            val streak = db.DailyStreakDao().getStreak(userId)
            _dailyStreak.postValue(streak)
        }
    }

    fun observeTotalExpenses(userId: Int): LiveData<Double?> {
        return liveData {
            emit(expenseDao.getTotalExpenses(userId))
        }
    }

    /*Code Attribution
   * View Model Scope
   * Android Developers(2024)*/
    fun getTransactionCount(userId: Int): LiveData<Int> {
        return object : LiveData<Int>() {
            override fun onActive() {
                super.onActive()
                viewModelScope.launch {
                    val count = expenseDao.getTransactionCount(userId)
                    postValue(count)
                }
            }
        }
    }
}