package com.f2h.f2h_buyer.screens.notification

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.NotificationDatabaseDao
import com.f2h.f2h_buyer.database.NotificationEntity
import com.f2h.f2h_buyer.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: NotificationDatabaseDao by lazy { F2HDatabase.getInstance(application).notificationDatabaseDao }
    private val viewModelFactory: NotificationViewModelFactory by lazy { NotificationViewModelFactory(dataSource, application) }
    private val viewModel: NotificationViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(
        NotificationViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.notification_page_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.clearAll -> {
                viewModel.clearAll()
                viewModel.getNotification()
                return true
            }
        }
        return false
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Members List recycler view
        val adapter = NotificationsAdapter( ClearClickListener { uiDataElement ->
            clearNotificationButton(uiDataElement)
        })
        binding.notificationListRecyclerView.adapter = adapter

        viewModel.visibleUiData.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        //Toast Message
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        })
        binding.groupsSwipeRefresh.setOnRefreshListener {
            viewModel.getNotification()
            binding.groupsSwipeRefresh.isRefreshing = false
        }

    }

    private fun clearNotificationButton(uiDataElement: NotificationEntity) {

    }

}
