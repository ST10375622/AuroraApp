package com.fake.auroraapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class ExpenseAdapter : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffCallback()) {
    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtExpenseName)
        val txtAmount: TextView = view.findViewById(R.id.txtExpenseAmount)
        val txtDate: TextView = view.findViewById(R.id.txtExpenseDate)
        val imgReceipt: ImageView = view.findViewById(R.id.imgReceipt)
    }

    /*Inflates the layout for item expense
    * Code Attribution
    * RecyclerView.Adapter
    * Android Developer (2024)*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    /*Observes expenses by ExpenseViewHolder
    * Code Attribution
    * LiveData.observeForever()
    * Android Developer (2024)*/
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.txtName.text = expense.name
        holder.txtAmount.text = "R ${expense.amount}"
        holder.txtDate.text = expense.date

        if (!expense.receiptUri.isNullOrEmpty()) {
            holder.imgReceipt.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(File(expense.receiptUri))
                .into(holder.imgReceipt)
        } else {
            holder.imgReceipt.visibility = View.GONE
        }
    }
    /*Code Attribution
    * DiffUtil.ItemCallback
    * Android Developer (2024)*/
    class DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense) = oldItem == newItem
    }
}