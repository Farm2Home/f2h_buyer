package com.f2h.f2h_buyer.screens.group.group_tabs

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import androidx.lifecycle.Observer
import com.f2h.f2h_buyer.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao


class GroupDetailsTabsFragment : Fragment() {

    private lateinit var groupDetailsTabsAdapter: GroupDetailsTabsAdapter
    private lateinit var viewPager: ViewPager2
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }

    private val viewModelFactory: GroupDetailsTabsViewModelFactory by lazy { GroupDetailsTabsViewModelFactory(dataSource, application) }
    private val viewModel: GroupDetailsTabsViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(
        GroupDetailsTabsViewModel::class.java) }

    val args: GroupDetailsTabsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_group_details_tabs, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.group_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.exitGroup) {
            viewModel.onClickExitGroup()
            return true
        } else {
            return NavigationUI.onNavDestinationSelected(item!!, requireView().findNavController()) ||
                    super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        groupDetailsTabsAdapter = GroupDetailsTabsAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = groupDetailsTabsAdapter
        viewPager.setUserInputEnabled(false)


        //Set app bar title to group name here
        (context as AppCompatActivity).supportActionBar!!.title = args.groupName

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "Products"
                1 -> tab.text = "My Orders"
                2 -> tab.text = "Group Wallet"
            }
        }.attach()

        viewModel.hasExitGroup.observe(viewLifecycleOwner, Observer { hasExitGroup ->
            if (hasExitGroup){
                onExitGroup()
            }
        })

    }

    private fun onExitGroup() {
        requireView().let {
            val navController = Navigation.findNavController(it)
            navController.popBackStack()
            navController.navigate(R.id.groupsFragment)
        }
    }

}
