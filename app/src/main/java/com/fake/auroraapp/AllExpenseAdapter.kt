package com.fake.auroraapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

//makes use of recyclerview to display a list of expenses
//Code Attribution
//RecyclerView.Adapter
//Android Developers(2024)
class AllExpenseAdapter : RecyclerView.Adapter<AllExpenseAdapter.ExpensiveViewHolder>(){

    //mutable list that will hold all the Expense data items
    private val expense = mutableListOf<Expense>()

    //updates the adapters dataset with the new list of expenses and also refreshes the User Interface
    fun submitList(list: List<Expense>) {
        expense.clear()
    expense.addAll(list)
    notifyDataSetChanged()
    }

    //viewholder to hold the UI references for a single item view
    //Code Attribution
    //ViewHolder pattern
    //Android Developers(2024)
    inner class ExpensiveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName = view.findViewById<TextView>(R.id.txtExpenseName)
        val txtAmount = view.findViewById<TextView>(R.id.txtExpenseAmount)
        val txtDate = view.findViewById<TextView>(R.id.txtExpenseDate)
        val imgReceipt = view.findViewById<ImageView>(R.id.imgReceipt)
    }

    //inflates the layout - item_expense.xml for each list item
    //returns a new view holder
    //Code Attribution
    //LayoutInflater
    //Android Developers(2024)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensiveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpensiveViewHolder(view)
    }

    //binds data to each item in the recyclerView
    //Code Attribution
    //RecyclerView.Adapter
    //Android Developers(2024)
    override fun onBindViewHolder(holder: ExpensiveViewHolder, position: Int) {
        val expense = expense[position]
        holder.txtName.text = expense.name
        holder.txtAmount.text = "R ${expense.amount.toInt()}"
        holder.txtDate.text = expense.date

        //if the specific expense has an image glide is used to load the image into ImageView
        //Code Attribution
        //Glide
        //Bumptech (2024)
        if (!expense.receiptUri.isNullOrEmpty()){
            Glide.with(holder.itemView.context)
                .load(File(expense.receiptUri))
                .into(holder.imgReceipt)
            holder.imgReceipt.visibility = View.VISIBLE
        }//no image visable if the specific expense does not have an image
        else {
            holder.imgReceipt.visibility = View.GONE
        }
    }

    override fun getItemCount() = expense.size
}