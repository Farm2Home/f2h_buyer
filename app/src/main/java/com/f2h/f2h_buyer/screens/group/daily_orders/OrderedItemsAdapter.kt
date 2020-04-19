package com.f2h.f2h_buyer.screens.group.daily_orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.databinding.ListOrderedItemsBinding

class OrderedItemsAdapter(val clickListener: OrderedItemClickListener): ListAdapter<DailyOrdersUiModel, OrderedItemsAdapter.ViewHolder>(ListItemDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder private constructor(val binding: ListOrderedItemsBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: DailyOrdersUiModel,
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

class ListItemDiffCallback : DiffUtil.ItemCallback<DailyOrdersUiModel>() {
    override fun areItemsTheSame(oldItem: DailyOrdersUiModel, newItem: DailyOrdersUiModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DailyOrdersUiModel, newItem: DailyOrdersUiModel): Boolean {
        return oldItem == newItem
    }
}

class OrderedItemClickListener(val clickListener: (item: DailyOrdersUiModel) -> Unit) {
    fun onClick(item: DailyOrdersUiModel) = clickListener(item)
}