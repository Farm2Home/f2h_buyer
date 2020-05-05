package com.f2h.f2h_buyer.screens.group.group_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.f2h.f2h_buyer.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class GroupDetailsTabsFragment : Fragment() {

    private lateinit var groupDetailsTabsAdapter: GroupDetailsTabsAdapter
    private lateinit var viewPager: ViewPager2
    val args: GroupDetailsTabsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_details_tabs, container, false)
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
                0 -> tab.text = "My Orders"
                1 -> tab.text = "All Items"
                2 -> tab.text = "Group Wallet"
            }
        }.attach()
    }

}
