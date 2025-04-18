package com.fake.auroraapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class BudgetViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BudgetRepository(
        db.userDao(),
        db .budgetDao(),
        db.categoryDao(),
        db.expenseDao()
    )

    suspend fun getUser(userId: Int): User? = repository.getUserById(userId)

    fun getBudget(userId: Int): LiveData<Budget> = repository.getBudget(userId)
    fun insertOrUpdateBudget(budget: Budget){
        viewModelScope.launch {
            repository.insertOrUpdateBudget(budget)
        }

    }

    fun getCategories(userId: Int): LiveData<List<Category>> = repository.getCategories(userId)
    fun insertCategory(category: Category){
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun deleteCategory(category: Category){
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun getExpenseByCategory(categoryId: Int): LiveData<List<Expense>> =
        repository.getExpenseByCategory(categoryId)

    fun getAllExpenses(): LiveData<List<Expense>> = repository.getAllExpenses()

    fun insertExpense(expense: Expense){
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense){
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}