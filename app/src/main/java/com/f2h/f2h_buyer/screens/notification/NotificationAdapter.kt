package com.f2h.f2h_buyer.screens.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.database.NotificationEntity
import com.f2h.f2h_buyer.databinding.ListNotificationBinding

class NotificationsAdapter(val clickListener: ClearClickListener):
    ListAdapter<NotificationEntity, NotificationsAdapter.ViewHolder>(GroupDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder private constructor(val binding: ListNotificationBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: NotificationEntity,
            clickListener: ClearClickListener
        ) {
            binding.notification = item
            binding.clearListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                val binding = ListNotificationBinding.inflate(view, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class GroupDiffCallback : DiffUtil.ItemCallback<NotificationEntity>() {
    override fun areItemsTheSame(oldItem: NotificationEntity, newItem: NotificationEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NotificationEntity, newItem: NotificationEntity): Boolean {
        return oldItem == newItem
    }
}

class ClearClickListener(val clickListener: (notificationEntity: NotificationEntity) -> Unit) {
    fun onClick(notificationEntity: NotificationEntity) = clickListener(notificationEntity)
}