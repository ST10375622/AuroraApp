package com.fake.auroraapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MonthlyReportAdapter : RecyclerView.Adapter<MonthlyReportAdapter.ExpenseViewHolder>() {

    private val expenses = mutableListOf<Expense>()

    fun submitList(list: List<Expense>) {
        expenses.clear()
        expenses.addAll(list)
        notifyDataSetChanged()
    }

    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName = view.findViewById<TextView>(R.id.textExpenseName)
        val txtAmount = view.findViewById<TextView>(R.id.textExpenseAmount)
        val txtDate = view.findViewById<TextView>(R.id.textExpenseDate)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_expense, parent, false)
        return ExpenseViewHolder(view)
    }
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.txtName.text = expense.name
        holder.txtAmount.text = "R ${expense.amount.toInt()}"
        holder.txtDate.text = expense.date
    }
    override fun getItemCount() = expenses.size

}