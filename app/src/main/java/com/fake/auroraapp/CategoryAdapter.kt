package com.fake.auroraapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Locale

interface ExpenseImagePicker{
    fun pickImageForCategory(categoryId: Int)
}

class CategoryAdapter (
    private val viewModel: BudgetViewModel,
    private val context: Context,
    private val imagePickerCallback : ExpenseImagePicker
): ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    inner class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtCategoryName: TextView = view.findViewById(R.id.txtCategoryName)
        val btnAddExpense: ImageButton = view.findViewById(R.id.btnAddExpense)
        val recyclerExpenses: RecyclerView = view.findViewById(R.id.recyclerExpenses)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.txtCategoryName.text = category.name
        val expenseAdapter = ExpenseAdapter()
        holder.recyclerExpenses.layoutManager = LinearLayoutManager(context)
        holder.recyclerExpenses.adapter = expenseAdapter

        viewModel.getExpenseByCategory(category.id).observeForever { expenses ->
            expenseAdapter.submitList(expenses)
        }
        holder.btnAddExpense.setOnClickListener {
            showAddExpenseDialog(category.id)
        }
    }
    private fun showAddExpenseDialog(categoryId: Int, imageUri: String? = null) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_expense, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.editExpenseName)
        val descInput = dialogView.findViewById<EditText>(R.id.editExpenseDesc)
        val amountInput = dialogView.findViewById<EditText>(R.id.editExpenseAmount)
        val dateInput = dialogView.findViewById<TextView>(R.id.textExpenseDate)
        val photoButton = dialogView.findViewById<Button>(R.id.btnUploadReciept)

        var receiptUri: String? = null
        val calendar = Calendar.getInstance()
        dateInput.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        dateInput.setOnClickListener {
            DatePickerDialog(
                context, { _, year, month, day ->
                    val date = "$year-${month + 1}-$day"
                    dateInput.text = date
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        photoButton.setOnClickListener {
             imagePickerCallback.pickImageForCategory(categoryId)
        }

        AlertDialog.Builder(context)
            .setTitle("Add Expense")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val expense = Expense(
                    categoryId = categoryId,
                    name = nameInput.text.toString(),
                    description = descInput.text.toString(),
                    amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0,
                    date = dateInput.text.toString(),
                    receiptUri = receiptUri
                )
                viewModel.insertExpense(expense)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}



