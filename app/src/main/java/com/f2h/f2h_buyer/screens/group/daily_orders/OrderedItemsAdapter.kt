package com.f2h.f2h_buyer.screens.group.daily_orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.databinding.ListOrderedItemsBinding
import com.f2h.f2h_buyer.network.models.Item

class OrderedItemsAdapter(val clickListener: OrderedItemClickListener): ListAdapter<DailyOrdersModel, OrderedItemsAdapter.ViewHolder>(ListItemDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder private constructor(val binding: ListOrderedItemsBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: DailyOrdersModel,
            clickListener: OrderedItemClickListener
        ) {
            binding.uiModel = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                val binding = ListOrderedItemsBinding.inflate(view, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ListItemDiffCallback : DiffUtil.ItemCallback<DailyOrdersModel>() {
    override fun areItemsTheSame(oldItem: DailyOrdersModel, newItem: DailyOrdersModel): Boolean {
        return oldItem.item.itemId == newItem.item.itemId
    }

    override fun areContentsTheSame(oldItem: DailyOrdersModel, newItem: DailyOrdersModel): Boolean {
        return oldItem == newItem
    }
}

class OrderedItemClickListener(val clickListener: (item: DailyOrdersModel) -> Unit) {
    fun onClick(item: DailyOrdersModel) = clickListener(item)
}