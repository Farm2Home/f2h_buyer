package com.f2h.f2h_buyer.screens.contact_us

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.databinding.FragmentContactUsBinding

/**
 * A simple [Fragment] subclass.
 */
class ContactUsFragment : Fragment() {

    private lateinit var binding: FragmentContactUsBinding
    private val viewModel: ContactUsViewModel by lazy {
        ViewModelProvider(this).get(ContactUsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        return binding.root
    }

}
