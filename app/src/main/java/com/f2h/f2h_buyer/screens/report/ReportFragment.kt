package com.f2h.f2h_buyer.screens.report

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentReportBinding
import com.f2h.f2h_buyer.databinding.FragmentReportBindingImpl
import com.google.android.material.datepicker.MaterialDatePicker

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: ReportViewModelFactory by lazy {
        ReportViewModelFactory(
            dataSource,
            application
        )
    }
    private val viewModel: ReportViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            ReportViewModel::class.java
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Daily Orders List recycler view
        val adapter = ReportItemsAdapter(OrderedItemClickListener { uiDataElement ->
            println("Clicked Report Item")
        })
        binding.reportListRecyclerView.adapter = adapter
        viewModel.visibleUiData.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })


        //Progress Bar loader
        viewModel.isProgressBarActive.observe(viewLifecycleOwner, Observer { isProgressBarActive ->
            if (isProgressBarActive) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })


        //Item Spinner
        binding.itemSelector?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.onItemSelected(position)
            }
        }


        //Order Status Spinner
        binding.orderStatusSelector?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.onOrderStatusSelected(position)
            }
        }



        //Payment Status Spinner
        binding.paymentStatusSelector?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.onPaymentStatusSelected(position)
            }
        }



        //End Date Selector Spinner
        binding.endDateSelector?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.onEndDateSelected(position)
            }
        }

        //Buyer Selector Spinner
        binding.buyerNameSelector?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.onBuyerSelected(position)
            }
        }

    }

}
