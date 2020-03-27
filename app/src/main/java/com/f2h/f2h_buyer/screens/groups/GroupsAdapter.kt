package com.f2h.f2h_buyer.screens.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.network.models.Group

class GroupsAdapter: RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {

    var data = listOf<Group>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.groupName.text = item.groupName
        holder.groupDescription.text = item.description
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_groups, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.groupName)
        val groupDescription: TextView = itemView.findViewById(R.id.groupDescription)
    }

}