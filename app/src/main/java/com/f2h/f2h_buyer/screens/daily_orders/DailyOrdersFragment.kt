package com.f2h.f2h_buyer.screens.daily_orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.databinding.FragmentDailyOrdersBinding

/**
 * A simple [Fragment] subclass.
 */
class DailyOrdersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDailyOrdersBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_daily_orders, container, false)
        return binding.root
    }
}
