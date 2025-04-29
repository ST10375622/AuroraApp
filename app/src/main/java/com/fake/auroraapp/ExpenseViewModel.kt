package com.fake.auroraapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseDao = AppDatabase.getDatabase(application).expenseDao()
    private val db = AppDatabase.getDatabase(application)
    private val treeProgressDao = AppDatabase.getDatabase(application).TreeProgressDao()

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

    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.insertExpense(expense)

            val total = expenseDao.getTotalExpenses(expense.userId) ?: 0.0
            val progressStage = when {
                total < 100 -> 1
                total < 300 -> 2
                total < 600 -> 3
                total < 1000 -> 4
                else -> 5
            }

            val progress = TreeProgress(
                userId = expense.userId,
                progress = progressStage,
                timestamp = System.currentTimeMillis().toString()
            )

            db.TreeProgressDao().insertProgress(progress)
        }
    }

    fun observeTotalExpenses(userId: Int): LiveData<Double?> {
        return liveData {
            emit(expenseDao.getTotalExpenses(userId))
        }
    }

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