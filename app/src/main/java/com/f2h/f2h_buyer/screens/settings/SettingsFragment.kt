package com.f2h.f2h_buyer.screens.settings

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
import com.f2h.f2h_buyer.databinding.FragmentGroupsBinding
import com.f2h.f2h_buyer.databinding.FragmentSettingsBinding

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: SettingsViewModelFactory by lazy { SettingsViewModelFactory(dataSource, application) }
    private val viewModel: SettingsViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        return binding.root
    }

}
