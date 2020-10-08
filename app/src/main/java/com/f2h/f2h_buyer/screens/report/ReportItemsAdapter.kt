package com.f2h.f2h_buyer.screens.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f2h.f2h_buyer.databinding.ListHeaderDateBinding
import com.f2h.f2h_buyer.databinding.ListReportItemsBinding
import com.f2h.f2h_buyer.databinding.ListServiceOrdersBinding
import com.f2h.f2h_buyer.screens.group.daily_orders.ServiceOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1
private const val ITEM_VIEW_TYPE_SERVICE = 2

class ReportItemsAdapter(val clickListener: OrderedItemClickListener):
    ListAdapter<DataItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<ReportItemsHeaderModel>) {
        adapterScope.launch {
            val items = when (list) {
                else -> createListWithHeaders(list)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }


    private fun createListWithHeaders(list: List<ReportItemsHeaderModel>): ArrayList<DataItem> {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val headers = list.sortedBy { df.parse(it.deliveryDate).time }
        val itemsListWithHeaders = arrayListOf<DataItem>()
        headers.forEach { header ->
            val ordersForDate = header.orders.map { DataItem.ReportOrdersItem(it) }
            val serviceForDate = header.serviceOrders.map { DataItem.ServiceOrderItem(it) }
            val amountForDay = header.totalAmount

            itemsListWithHeaders.add(
                DataItem.Header(header.deliveryDate, amountForDay,
                header.orderHeaderId, header.packingNumber))
            itemsListWithHeaders.addAll(ordersForDate)
            itemsListWithHeaders.addAll(serviceForDate)
        }
        return itemsListWithHeaders
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                val reportOrdersItem = getItem(position) as DataItem.ReportOrdersItem
                holder.bind(reportOrdersItem.reportItemModel, clickListener)
            }
            is HeaderViewHolder -> {
                val header = getItem(position) as DataItem.Header
                holder.bind(header.date, header.amount, header.packingNumber)
            }
            is ServiceViewHolder -> {
                val service = getItem(position) as DataItem.ServiceOrderItem
                holder.bind(service.serviceOrder)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ItemViewHolder.from(parent)
            ITEM_VIEW_TYPE_SERVICE -> ServiceViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.ReportOrdersItem -> ITEM_VIEW_TYPE_ITEM
            is DataItem.ServiceOrderItem -> ITEM_VIEW_TYPE_SERVICE
        }
    }

    class HeaderViewHolder private constructor(val binding: ListHeaderDateBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(
            date: String,
            amount: Double,
            packingNumber: Long
        ) {
            binding.date = date
            binding.amount = amount
            binding.packingNumber = packingNumber
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListHeaderDateBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    class ServiceViewHolder private constructor(val binding: ListServiceOrdersBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ServiceOrder
        ) {
            binding.uiModel = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ServiceViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListServiceOrdersBinding.inflate(layoutInflater, parent, false)
                return ServiceViewHolder(view)
            }
        }
    }

    class ItemViewHolder private constructor(val binding: ListReportItemsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ReportItemsModel,
            clickListener: OrderedItemClickListener
        ) {
            binding.uiModel = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListReportItemsBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(view)
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


class OrderedItemClickListener(val clickListener: (uiModel: ReportItemsModel) -> Unit) {
    fun onClick(uiModel: ReportItemsModel) = clickListener(uiModel)
}


sealed class DataItem {
    abstract val id: Long
    data class ReportOrdersItem(val reportItemModel: ReportItemsModel): DataItem() {
        override val id = reportItemModel.orderId
    }

    data class Header(val date: String, val amount: Double, val orderHeaderId: Long, val packingNumber: Long) : DataItem() {
        override val id = orderHeaderId
    }

    data class ServiceOrderItem(val serviceOrder: ServiceOrder) : DataItem() {
        override val id = serviceOrder.orderId
    }
}

