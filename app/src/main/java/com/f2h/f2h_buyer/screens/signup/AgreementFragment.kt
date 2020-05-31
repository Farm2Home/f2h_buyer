package com.f2h.f2h_buyer.screens.signup

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.databinding.FragmentAgreementBinding

class AgreementFragment: Fragment() {

    private lateinit var binding: FragmentAgreementBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_agreement, container, false)
        binding.setLifecycleOwner(this)

        binding.accept.setOnClickListener {
            onAgreementAccepted()
        }

        return binding.root
    }

    private fun onAgreementAccepted() {
        Toast.makeText(this.context, "Accepted the terms and conditions", Toast.LENGTH_SHORT).show()
        val action = AgreementFragmentDirections.actionAgreementFragmentToSignUpFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

}