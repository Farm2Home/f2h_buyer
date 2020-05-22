package com.f2h.f2h_buyer.screens.search_group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.databinding.ListGroupsBinding
import com.f2h.f2h_buyer.databinding.ListSearchGroupsBinding
import com.f2h.f2h_buyer.network.models.Group

class SearchGroupsAdapter(val clickListener: GroupClickListener): ListAdapter<SearchGroupsItemsModel, SearchGroupsAdapter.ViewHolder>(GroupDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder private constructor(val binding: ListSearchGroupsBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SearchGroupsItemsModel,
            clickListener: GroupClickListener
        ) {
            binding.group = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                val binding = ListSearchGroupsBinding.inflate(view, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class GroupDiffCallback : DiffUtil.ItemCallback<SearchGroupsItemsModel>() {
    override fun areItemsTheSame(oldItem: SearchGroupsItemsModel, newItem: SearchGroupsItemsModel): Boolean {
        return oldItem.groupId == newItem.groupId
    }

    override fun areContentsTheSame(oldItem: SearchGroupsItemsModel, newItem: SearchGroupsItemsModel): Boolean {
        return oldItem == newItem
    }
}

class GroupClickListener(val clickListener: (group: SearchGroupsItemsModel) -> Unit) {
    fun onClick(group: SearchGroupsItemsModel) = clickListener(group)
}