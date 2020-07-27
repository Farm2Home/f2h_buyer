package com.f2h.f2h_buyer.screens.group.daily_orders

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentDailyOrdersBinding
import com.f2h.f2h_buyer.screens.group.group_tabs.GroupDetailsTabsFragmentDirections
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
        binding = inflate(inflater, R.layout.fragment_daily_orders, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.dailyOrdersSwipeRefresh.setOnRefreshListener {
            viewModel.getItemsAndAvailabilitiesForUser()
            binding.dailyOrdersSwipeRefresh.isRefreshing = false
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Daily Orders List recycler view
        val adapter = OrderedItemsAdapter(OrderedItemClickListener { uiDataElement ->
            navigateToPreOrderPage(uiDataElement)
        }, IncreaseButtonClickListener { uiDataElement ->
            viewModel.increaseOrderQuantity(uiDataElement)
        }, DecreaseButtonClickListener { uiDataElement ->
            viewModel.decreaseOrderQuantity(uiDataElement)
        })
        binding.itemListRecyclerView.adapter = adapter
        viewModel.visibleUiData.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
                adapter.notifyDataSetChanged()
            }
        })


        //Toast Message
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        })

    }


    override fun onResume() {
        super.onResume()
        viewModel.getItemsAndAvailabilitiesForUser()
    }

    private fun navigateToPreOrderPage(uiData: DailyOrdersUiModel) {
        val action = GroupDetailsTabsFragmentDirections.actionGroupDetailsTabsFragmentToPreOrderFragment(uiData.itemId)
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

}
