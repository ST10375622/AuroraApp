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

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    fun getCategories(userId: Int): LiveData<List<Category>> = categoryDao.getCategoriesByUser(userId)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    fun getExpenseByCategory(categoryId: Int): LiveData<List<Expense>> = expenseDao.getExpenseByCategory(categoryId)
    fun getAllExpenses(): LiveData<List<Expense>> = expenseDao.getAllExpense()
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
}