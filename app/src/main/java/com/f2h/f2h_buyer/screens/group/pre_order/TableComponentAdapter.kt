package com.f2h.f2h_buyer.screens.group.pre_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.databinding.TableComponentBinding

class TableComponentAdapter(val clickListener: TableComponentClickListener): ListAdapter<TableComponent, TableComponentAdapter.ViewHolder>(
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


    class ViewHolder private constructor(val binding: TableComponentBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(
            tableRow: TableComponent?,
            clickListener: TableComponentClickListener
        ) {
            binding.tableRow = tableRow
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                val binding = TableComponentBinding.inflate(view, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }
    }
}

class TableComponentDiffCallback : DiffUtil.ItemCallback<TableComponent>() {
    override fun areItemsTheSame(oldItem: TableComponent, newItem: TableComponent): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TableComponent, newItem: TableComponent): Boolean {
        return oldItem == newItem
    }
}

class TableComponentClickListener(val clickListener: (row: TableComponent) -> Unit) {
    fun onClick(row: TableComponent) = clickListener(row)
}