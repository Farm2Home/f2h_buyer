package com.f2h.f2h_buyer.screens.group.pre_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.databinding.ListPreorderItemBinding

class PreOrderItemsAdapter(val clickListener: PreOrderItemClickListener): ListAdapter<PreOrderItemsModel, PreOrderItemsAdapter.ViewHolder>(
    TableComponentDiffCallback()
) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }


    class ViewHolder private constructor(val binding: ListPreorderItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(
            uiItemsModel: PreOrderItemsModel?,
            clickListener: PreOrderItemClickListener
        ) {
            binding.uiModel = uiItemsModel
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                val binding = ListPreorderItemBinding.inflate(view, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }
    }
}

class TableComponentDiffCallback : DiffUtil.ItemCallback<PreOrderItemsModel>() {
    override fun areItemsTheSame(oldItem: PreOrderItemsModel, newItem: PreOrderItemsModel): Boolean {
        return oldItem.itemAvailabilityId == newItem.itemAvailabilityId
    }

    override fun areContentsTheSame(oldItem: PreOrderItemsModel, newItem: PreOrderItemsModel): Boolean {
        return oldItem == newItem
    }
}

class PreOrderItemClickListener(val clickListener: (row: PreOrderItemsModel) -> Unit) {
    fun onClick(row: PreOrderItemsModel) = clickListener(row)
}