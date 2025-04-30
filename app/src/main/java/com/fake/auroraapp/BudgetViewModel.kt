package com.fake.auroraapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BudgetViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BudgetRepository(
        db.userDao(),
        db.budgetDao(),
        db.categoryDao(),
        db.expenseDao()
    )
    private val notificationRepository = NotificationRepository(db.notificationDao())

    /*Retrieves a single user
    * observes the users budget using LiveData
    * updates the database using viewModelScope
    * Code Attribution
    * Coroutines and ViewModelScope
    * Kotlin Foundation(2024)*/
    suspend fun getUser(userId: Int): User? = repository.getUserById(userId)
    fun getBudget(userId: Int): LiveData<Budget> = repository.getBudget(userId)
    fun insertOrUpdateBudget(budget: Budget){ viewModelScope.launch { repository.insertOrUpdateBudget(budget) } }

    //supports the operations for the categories and expenses
    fun getCategories(userId: Int): LiveData<List<Category>> = repository.getCategories(userId)
    fun insertCategory(category: Category){ viewModelScope.launch { repository.insertCategory(category) } }
    fun deleteCategory(category: Category){ viewModelScope.launch { repository.deleteCategory(category) } }
    fun insertExpense(expense: Expense){ viewModelScope.launch { repository.insertExpense(expense)
            val budget = repository.getBudgetValue(expense.userId)
            if (budget != null) {
                updateAmountLeft(expense.userId, budget.monthlyBudget, budget.minimumBudget)
            }
        }
    }
    fun deleteExpense(expense: Expense){ viewModelScope.launch { repository.deleteExpense(expense)
            val budget = repository.getBudgetValue(expense.userId)
            if (budget != null) {
                updateAmountLeft(expense.userId, budget.monthlyBudget, budget.minimumBudget)
            }
        }
    }

    fun getExpenseByCategory(categoryId: Int): LiveData<List<Expense>> =
        repository.getExpenseByCategory(categoryId)

    fun getAllExpenses(): LiveData<List<Expense>> = repository.getAllExpenses()

    /* stores messages into the notification table
    * uses liveData to push updates to the User Interface
    *Code Attribution
    *LiveData
    *Android Developers(2024)*/
    fun insertNotification(notification: Notification) { viewModelScope.launch { notificationRepository.insertNotification(notification) } }
    fun getAllNotifications(): LiveData<List<Notification>> { return notificationRepository.getAllNotification() }

    //calculates amount left
    //triggers the notification if spending exceeds the minimum amount
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

            if (amountLeft <= minimumBudget) {
                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val warningMessage = "Warning: Your expenses have reached the minimum budget limit!"

                val notification = Notification(
                    message = warningMessage,
                    date = currentDate
                )
                insertNotification(notification)
            }
        }
    }

    /*Code Attribution
    * View Model
    * Android Developers(2024)*/
    fun getExpensesForMonth(month: Int, year: Int): LiveData<List<Expense>> {
        val formattedMonth = String.format("%02d", month)
        return repository.getExpensesByMonth(formattedMonth, year.toString())
    }
    suspend fun getTopSpendingCategory(month: Int, year: Int): TopCategory? {
        val m = String.format("%02d", month)
        return repository.getTopCategory(m, year.toString())
    }
    suspend fun getTotalSpent(month: Int, year: Int): Double {
        val m = String.format("%02d", month)
        return repository.getTotalSpent(m, year.toString()) ?: 0.0
    }
    suspend fun getTransactionCount(month: Int, year: Int): Int {
        val m = String.format("%02d", month)
        return repository.getTransactionCount(m, year.toString())
    }

    suspend fun getCategoryName(categoryId: Int): String? {
        return repository.getCategoryNameById(categoryId)
    }
}