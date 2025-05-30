package com.fake.auroraapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fake.auroraapp.databinding.ItemNotificationBinding

class NotificationAdapter: ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(DiffCallback()) {

class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val messageText: TextView = itemView.findViewById(R.id.textViewNotificationMessage)
    val dateText: TextView = itemView.findViewById(R.id.textViewNotificationDate)
}

    /*Inflates the layout for the notification
   * Code Attribution
   * RecyclerView.Adapter
   * Android Developer (2024)*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    /*Observes notifications
    * Code Attribution
    * LiveData.observeForever()
    * Android Developer (2024)*/
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = getItem(position)
        holder.messageText.text = notification.message
        holder.dateText.text = notification.date
    }

    /*Code Attribution
    * DiffUtil.ItemCallback
    * Android Developer (2024)*/
    class DiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
}