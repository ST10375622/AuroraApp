package com.fake.auroraapp

import androidx.lifecycle.LiveData

class BudgetRepository (
    private val userDao: UserDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao
) {
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    suspend fun getUserById(userId: Int): User? = userDao.getUserById(userId)

    fun getBudget(userId: Int): LiveData<Budget> = budgetDao.getBudget(userId)
    suspend fun insertOrUpdateBudget(budget: Budget) = budgetDao.insertOrUpdateBudget(budget)
    suspend fun getTotalExpense(userId: Int): Double{
        return expenseDao.getTotalExpenses(userId) ?: 0.0
    }

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    fun getCategories(userId: Int): LiveData<List<Category>> = categoryDao.getCategoriesByUser(userId)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    fun getExpenseByCategory(categoryId: Int): LiveData<List<Expense>> = expenseDao.getExpenseByCategory(categoryId)
    fun getAllExpenses(): LiveData<List<Expense>> = expenseDao.getAllExpense()
    fun getExpensesByMonth(month: String, year: String): LiveData<List<Expense>>{
        return expenseDao.getExpensesByMonth(month, year)
    }
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
    suspend fun getBudgetValue(userId: Int): Budget? = budgetDao.getBudgetValue(userId)

    suspend fun getTopCategory(month: String, year: String): TopCategory? {
        return expenseDao.getTopCategory(month, year)
    }

    suspend fun getTotalSpent(month: String, year: String): Double? {
        return  expenseDao.getTotalSpent(month, year)
    }

    suspend fun getTransactionCount(month: String, year: String): Int {
        return expenseDao.getTransactionCount(month, year)
    }

    suspend fun getCategoryNameById(categoryId: Int): String? {
        return categoryDao.getCategoryNameById(categoryId)
    }
}