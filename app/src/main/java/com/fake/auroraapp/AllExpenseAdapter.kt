package com.fake.auroraapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class AllExpenseAdapter : RecyclerView.Adapter<AllExpenseAdapter.ExpensiveViewHolder>(){

    private val expense = mutableListOf<Expense>()

    fun submitList(list: List<Expense>) {
        expense.clear()
    expense.addAll(list)
    notifyDataSetChanged()
    }

    inner class ExpensiveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName = view.findViewById<TextView>(R.id.txtExpenseName)
        val txtAmount = view.findViewById<TextView>(R.id.txtExpenseAmount)
        val txtDate = view.findViewById<TextView>(R.id.txtExpenseDate)
        val imgReceipt = view.findViewById<ImageView>(R.id.imgReceipt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensiveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpensiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpensiveViewHolder, position: Int) {
        val expense = expense[position]
        holder.txtName.text = expense.name
        holder.txtAmount.text = "R ${expense.amount.toInt()}"
        holder.txtDate.text = expense.date

        if (!expense.receiptUri.isNullOrEmpty()){
            Glide.with(holder.itemView.context)
                .load(File(expense.receiptUri))
                .into(holder.imgReceipt)
            holder.imgReceipt.visibility = View.VISIBLE
        } else {
            holder.imgReceipt.visibility = View.GONE
        }
    }

    override fun getItemCount() = expense.size
}