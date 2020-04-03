package com.f2h.f2h_buyer.screens.daily_orders

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentDailyOrdersBinding
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class DailyOrdersFragment : Fragment() {

    private lateinit var binding: FragmentDailyOrdersBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: DailyOrdersViewModelFactory by lazy { DailyOrdersViewModelFactory(dataSource, application) }
    private val viewModel: DailyOrdersViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(
        DailyOrdersViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_orders, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        viewModel.refreshFragmentData()

        val adapter = ItemsAdapter(ItemClickListener { item ->
            Toast.makeText(context, "Item selected : " + item.itemName, Toast.LENGTH_SHORT).show()
        })
        binding.itemListRecyclerView.adapter = adapter
        viewModel.items.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })



        // Settings for the horizontal calendar
        val startDate: Calendar = Calendar.getInstance()
        startDate.add(Calendar.DATE, 0)
        val endDate: Calendar = Calendar.getInstance()
        endDate.add(Calendar.DATE, 7)

        val horizontalCalendar: HorizontalCalendar = HorizontalCalendar.Builder(binding.root, R.id.calendarView)
            .range(startDate, endDate)
            .datesNumberOnScreen(5)
            .configure()
            .textSize(12F,20F,12F)
            .end()
            .build()

        horizontalCalendar.setCalendarListener(object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                Toast.makeText(context, "Date selected : " + date?.time, Toast.LENGTH_SHORT).show()
            }
        })


        return binding.root
    }
}
