package com.f2h.f2h_buyer.screens.refer

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation

import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentSignoutBinding
import com.f2h.f2h_buyer.screens.MainActivity
import com.f2h.f2h_buyer.screens.group_list.GroupsFragmentDirections
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class ReferFragment : Fragment() {

    private lateinit var binding: FragmentSignoutBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signout, container, false)
        binding.setLifecycleOwner(this)
        share()
        return binding.root
    }

    private fun share (){
        var text = "Hi - I am buying from neighbourhood farmers through  VILLAGE VEGGYS application. It might be useful for you as well. \n" +
                "\n" +
                "Download the application from Playstore https://play.google.com/store/apps/details?id=com.f2h.f2h_buyer"
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(sendIntent)
        view?.let { Navigation.findNavController(it).popBackStack() }
//        val action = ReferFragmentDirections.actionReferFragmentToGroupsFragment()
//        view?.let { Navigation.findNavController(it).navigate(action) }
    }


}
