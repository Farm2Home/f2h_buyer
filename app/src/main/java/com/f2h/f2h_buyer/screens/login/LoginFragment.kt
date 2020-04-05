package com.f2h.f2h_buyer.screens.login

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentLoginBinding
import com.f2h.f2h_buyer.screens.UserPagesActivity

class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: LoginViewModelFactory by lazy { LoginViewModelFactory(dataSource, application) }
    private val viewModel: LoginViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.isLoginComplete.observe(viewLifecycleOwner, Observer { isLoginComplete ->
            if(isLoginComplete) {
                onLoginComplete()
            }
        })

        viewModel.isProgressBarActive.observe(viewLifecycleOwner, Observer { isProgressBarActive ->
            if(isProgressBarActive){
                binding.progressBar.visibility = View.VISIBLE
                binding.loginButton.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.loginButton.visibility = View.VISIBLE
            }
        })

        return binding.root
    }

    private fun onLoginComplete() {
        if (viewModel.loginResponse.value != null) {
            Toast.makeText(this.context, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this.context, UserPagesActivity::class.java)
             startActivity(intent)
        } else {
            Toast.makeText(this.activity, "Please Login Again", Toast.LENGTH_SHORT).show()
        }
    }

}