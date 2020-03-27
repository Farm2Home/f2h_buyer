package com.f2h.f2h_buyer.screens.login

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
import com.f2h.f2h_buyer.databinding.FragmentLoginBinding
import com.f2h.f2h_buyer.screens.groups.GroupsActivity

class LoginFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = F2HDatabase.getInstance(application).sessionDatabaseDao
        val viewModelFactory = LoginViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)


        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.isLoginComplete.observe(viewLifecycleOwner, Observer { isLoginComplete ->
            if(isLoginComplete) {
                onLoginComplete(viewModel)
            }
        })

        return binding.root
    }

    private fun onLoginComplete(viewModel: LoginViewModel) {
        if (viewModel.loginResponse.value != null) {
            Toast.makeText(this.context, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this.context, GroupsActivity::class.java)
             startActivity(intent)
        } else {
            Toast.makeText(this.activity, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }

}