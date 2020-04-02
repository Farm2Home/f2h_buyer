package com.f2h.f2h_buyer.screens.daily_orders

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentDailyOrdersBinding

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

        return binding.root
    }
}
