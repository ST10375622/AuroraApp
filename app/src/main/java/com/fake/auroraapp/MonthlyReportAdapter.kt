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

    /*this is an inner class
    * holds references to the Expense item user interface
    * Code Attribution
    * ViewHolder Pattern
    * Android Developer (2024)*/
    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName = view.findViewById<TextView>(R.id.textExpensesName)
        val txtAmount = view.findViewById<TextView>(R.id.textExpensesAmount)
        val txtDate = view.findViewById<TextView>(R.id.textExpensesDate)
    }
    /*Inflates the layout for the monthly expense
    * Code Attribution
    * RecyclerView.Adapter
    * Android Developer (2024)*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_expense, parent, false)
        return ExpenseViewHolder(view)
    }
    /*Observes expenses
    * Code Attribution
    * LiveData.observeForever()
    * Android Developer (2024)*/
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.txtName.text = expense.name
        holder.txtAmount.text = "R ${expense.amount.toInt()}"
        holder.txtDate.text = expense.date
    }
    override fun getItemCount() = expenses.size

}