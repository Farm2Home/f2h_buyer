package com.f2h.f2h_buyer.screens.sign_out

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentSignoutBinding
import com.f2h.f2h_buyer.screens.MainActivity
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class SignOutFragment : Fragment() {

    private lateinit var binding: FragmentSignoutBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val database: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signout, container, false)
        binding.setLifecycleOwner(this)
        tryToLogout()
        return binding.root
    }

    private fun tryToLogout (){
        coroutineScope.launch {
            clearSession()
        }
        Toast.makeText(this.context, "Sign Out Successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(this.context, MainActivity::class.java)
        startActivity(intent)
    }

    private suspend fun clearSession() {
        return withContext(Dispatchers.IO) {
            database.clearSessions()
        }
    }

}
