package com.f2h.f2h_buyer.screens.report

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
import com.f2h.f2h_buyer.databinding.FragmentReportBinding
import com.f2h.f2h_buyer.databinding.FragmentReportBindingImpl

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: ReportViewModelFactory by lazy { ReportViewModelFactory(dataSource, application) }
    private val viewModel: ReportViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(ReportViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        return binding.root
    }

}
