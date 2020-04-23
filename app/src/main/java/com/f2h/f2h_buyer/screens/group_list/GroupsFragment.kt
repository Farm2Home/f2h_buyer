package com.f2h.f2h_buyer.screens.group_list

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation

import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentGroupsBinding
import com.f2h.f2h_buyer.network.models.Group


/**
 * A simple [Fragment] subclass.
 */
class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: GroupsViewModelFactory by lazy { GroupsViewModelFactory(dataSource, application) }
    private val viewModel: GroupsViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(GroupsViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups , container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        val adapter = GroupsAdapter(GroupClickListener { group ->
            onGroupSelected(group)
        })
        binding.groupListRecyclerView.adapter = adapter
        viewModel.group.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        //Set app bar title to group name here
        (context as AppCompatActivity).supportActionBar!!.title = "Farm To Home"

        return binding.root
    }


    fun onGroupSelected(group: Group){
        viewModel.updateSessionWithGroupInfo(group)
        val action = GroupsFragmentDirections.actionGroupsFragmentToGroupDetailsTabsFragment(group.groupName ?: "")
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

}
