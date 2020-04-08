package com.f2h.f2h_buyer.screens.group.group_wallet

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.databinding.FragmentGroupWalletBinding
import com.f2h.f2h_buyer.screens.group.group_tabs.GroupWalletViewModelFactory

class GroupWalletFragment : Fragment() {

    private lateinit var binding: FragmentGroupWalletBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val viewModelFactory: GroupWalletViewModelFactory by lazy { GroupWalletViewModelFactory(application) }
    private val viewModel: GroupWalletViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(
        GroupWalletViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_wallet, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        return binding.root    }

}
