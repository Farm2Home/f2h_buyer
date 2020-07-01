package com.f2h.f2h_buyer.screens.group.daily_orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.databinding.ListHeaderDateBinding
import com.f2h.f2h_buyer.databinding.ListOrderedItemsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class OrderedItemsAdapter(val clickListener: OrderedItemClickListener,
                          val increaseButtonClickListener: IncreaseButtonClickListener,
                          val decreaseButtonClickListener: DecreaseButtonClickListener):
    ListAdapter<DataItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<DailyOrdersUiModel>) {
        adapterScope.launch {
            val items = when (list) {
                else -> createListWithHeaders(list)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    private fun createListWithHeaders(list: List<DailyOrdersUiModel>): ArrayList<DataItem> {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        var uniqueDates =
            list.map { DataItem.DailyOrdersItem(it).dailyOrdersUiModel.orderedDate }.distinct()
        uniqueDates = uniqueDates.sortedBy { df.parse(it).time }
        val itemsListWithHeaders = arrayListOf<DataItem>()
        uniqueDates.stream().forEach { date ->
            itemsListWithHeaders.add(DataItem.Header(date))
            itemsListWithHeaders.addAll(list.filter{ date.equals(DataItem.DailyOrdersItem(it).dailyOrdersUiModel.orderedDate) }
                .map { DataItem.DailyOrdersItem(it) })
        }
        return itemsListWithHeaders
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val dailyOrdersItem = getItem(position) as DataItem.DailyOrdersItem
                holder.bind(dailyOrdersItem.dailyOrdersUiModel, clickListener, increaseButtonClickListener, decreaseButtonClickListener)
            }
            is TextViewHolder -> {
                val header = getItem(position) as DataItem.Header
                holder.bind(header.date)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.DailyOrdersItem -> ITEM_VIEW_TYPE_ITEM
        }
    }


    class TextViewHolder private constructor(val binding: ListHeaderDateBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(
            date: String
        ) {
            binding.date = date
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListHeaderDateBinding.inflate(layoutInflater, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    class ViewHolder private constructor(val binding: ListOrderedItemsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: DailyOrdersUiModel,
            clickListener: OrderedItemClickListener,
            increaseButtonClickListener: IncreaseButtonClickListener,
            decreaseButtonClickListener: DecreaseButtonClickListener
        ) {
            binding.uiModel = item
            binding.clickListener = clickListener
            binding.increaseButtonClickListener = increaseButtonClickListener
            binding.decreaseButtonClickListener = decreaseButtonClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListOrderedItemsBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(view)
            }
        }
    }
}


class ListItemDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


class OrderedItemClickListener(val clickListener: (uiModel: DailyOrdersUiModel) -> Unit) {
    fun onClick(uiModel: DailyOrdersUiModel) = clickListener(uiModel)
}

class IncreaseButtonClickListener(val clickListener: (uiModel: DailyOrdersUiModel) -> Unit) {
    fun onClick(uiModel: DailyOrdersUiModel) = clickListener(uiModel)
}

class DecreaseButtonClickListener(val clickListener: (uiModel: DailyOrdersUiModel) -> Unit) {
    fun onClick(uiModel: DailyOrdersUiModel) = clickListener(uiModel)
}


sealed class DataItem {
    abstract val id: Long
    data class DailyOrdersItem(val dailyOrdersUiModel: DailyOrdersUiModel): DataItem() {
        override val id = dailyOrdersUiModel.orderId
    }

    data class Header(val date: String) : DataItem() {
        override val id = Long.MIN_VALUE
    }
}