package com.f2h.f2h_buyer.screens.group.group_details

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.f2h.f2h_buyer.screens.group.all_items.AllItemsFragment
import com.f2h.f2h_buyer.screens.group.daily_orders.DailyOrdersFragment
import com.f2h.f2h_buyer.screens.group.group_wallet.GroupWalletFragment


class GroupDetailsTabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> {
                val fragment = DailyOrdersFragment()
                return fragment
            }

            1 -> {
                val fragment = AllItemsFragment()
                return fragment
            }

            2 -> {
                val fragment = GroupWalletFragment()
                return fragment
            }
        }

        val fragment = AllItemsFragment()
        return fragment
    }
}
