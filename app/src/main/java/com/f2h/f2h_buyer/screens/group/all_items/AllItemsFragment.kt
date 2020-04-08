package com.f2h.f2h_buyer.screens.group.all_items

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentAllItemsBinding
import com.f2h.f2h_buyer.screens.group.daily_orders.AllItemsViewModelFactory
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class AllItemsFragment : Fragment() {

    private lateinit var binding: FragmentAllItemsBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: AllItemsViewModelFactory by lazy { AllItemsViewModelFactory(dataSource, application) }
    private val viewModel: AllItemsViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(
        AllItemsViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, R.layout.fragment_all_items, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        viewModel.refreshFragmentData()


        // Item list recycler view
        val adapter = AllItemsAdapter(AllItemClickListener { item ->
            Toast.makeText(context, "Item selected : " + item.itemName, Toast.LENGTH_SHORT).show()
        })
        binding.itemListRecyclerView.adapter = adapter
        viewModel.visibleItems.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })



        // Progress Bar loader
        viewModel.isProgressBarActive.observe(viewLifecycleOwner, Observer { isProgressBarActive ->
            if(isProgressBarActive){
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })


        return binding.root
    }
}