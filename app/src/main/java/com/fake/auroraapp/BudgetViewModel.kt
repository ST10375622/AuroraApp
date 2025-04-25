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

            val budget = repository.getBudgetValue(expense.userId)
            if (budget != null) {
                updateAmountLeft(expense.userId, budget.monthlyBudget, budget.minimumBudget)
            }
        }
    }

    fun deleteExpense(expense: Expense){
        viewModelScope.launch {
            repository.deleteExpense(expense)

            val budget = repository.getBudgetValue(expense.userId)
            if (budget != null) {
                updateAmountLeft(expense.userId, budget.monthlyBudget, budget.minimumBudget)
            }
        }
    }

    fun updateAmountLeft(userId: Int, monthlyBudget: Double, minimumBudget: Double) {
        viewModelScope.launch {
            val totalExpense = repository.getTotalExpense(userId)
            val amountLeft = monthlyBudget - totalExpense

            val updatedBudget = Budget(
                userId = userId,
                monthlyBudget = monthlyBudget,
                minimumBudget = minimumBudget,
                amountLeft = amountLeft
            )
            insertOrUpdateBudget(updatedBudget)

        }
    }
}