package com.f2h.f2h_buyer.screens.group_list

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
//import androidx.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.NotificationDatabaseDao
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentGroupsBinding
import com.f2h.f2h_buyer.network.models.Group
import kotlin.math.absoluteValue


/**
 * A simple [Fragment] subclass.
 */
class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val notificationDataSource: NotificationDatabaseDao by lazy { F2HDatabase.getInstance(application).notificationDatabaseDao }
    private val viewModelFactory: GroupsViewModelFactory by lazy { GroupsViewModelFactory(dataSource, notificationDataSource, application) }
    private val viewModel: GroupsViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(GroupsViewModel::class.java) }
    private lateinit var unreadCount: TextView;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups , container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        viewModel.refreshFragmentData()
        val adapter = GroupsAdapter(GroupClickListener { group ->
            onGroupSelected(group)
        })
        binding.groupListRecyclerView.adapter = adapter
        viewModel.group.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.unreadCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                updateUnreadNotificationCount(it.absoluteValue)
            }
        })

        viewModel.isGroupListEmpty.observe(viewLifecycleOwner, Observer { isGroupListEmpty ->
            if(isGroupListEmpty){
                binding.emptyGroupsText.visibility = View.VISIBLE
                binding.joinButton.visibility = View.VISIBLE
            } else {
                binding.emptyGroupsText.visibility = View.GONE
                binding.joinButton.visibility = View.GONE
            }
        })
        binding.joinButton.setOnClickListener {
            onJoinGroupButtonClicked()
        }

        binding.groupsSwipeRefresh.setOnRefreshListener {
            viewModel.refreshFragmentData()
            binding.groupsSwipeRefresh.isRefreshing = false
        }

        //Set app bar title to group name here
        (context as AppCompatActivity).supportActionBar!!.title = "Village Veggys"
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main_menu, menu)
        val menu_notification_bell = menu.findItem(R.id.menu_notification).actionView
        unreadCount = menu_notification_bell.findViewById(R.id.count_notification) as TextView
        unreadCount.setOnClickListener{
            navigateToNotification()
        }
        (menu_notification_bell.findViewById(R.id.notification_bell) as ImageView).setOnClickListener{
            navigateToNotification()
        }

        updateUnreadNotificationCount(viewModel.unreadCount.value?:0)
    }

    fun updateUnreadNotificationCount(count: Int) {
        if (unreadCount == null) return
        getActivity()?.runOnUiThread(Runnable {
            if (count == 0) {
                unreadCount.visibility = View.INVISIBLE
            } else {
                unreadCount.visibility = View.VISIBLE
                unreadCount.text = Integer.toString(count)
            }
        })
    }


    fun navigateToNotification(): Boolean{
        val action = GroupsFragmentDirections.actionGroupsFragmentToNotificationFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
        return true
    }

    fun onGroupSelected(group: Group){
        viewModel.updateSessionWithGroupInfo(group)
        val action = GroupsFragmentDirections.actionGroupsFragmentToGroupDetailsTabsFragment(group.groupName ?: "")
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    fun onJoinGroupButtonClicked() {
        val action = GroupsFragmentDirections.actionGroupsFragmentToSearchGroupsFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }


}
