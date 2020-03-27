package com.f2h.f2h_buyer.screens.contact_us

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
import com.f2h.f2h_buyer.databinding.FragmentContactUsBinding
import com.f2h.f2h_buyer.screens.groups.GroupsViewModel
import com.f2h.f2h_buyer.screens.groups.GroupsViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class ContactUsFragment : Fragment() {

    private lateinit var binding: FragmentContactUsBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: ContactUsModelFactory by lazy { ContactUsModelFactory(dataSource, application) }
    private val viewModel: ContactUsViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(
        ContactUsViewModel::class.java) }

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
