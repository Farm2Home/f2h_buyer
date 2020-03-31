package com.f2h.f2h_buyer.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.databinding.ActivityUserPagesBinding
import kotlinx.android.synthetic.main.activity_user_pages.view.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserPagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPagesBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_pages)
        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.userPagesNavHostFragment)

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        val f2hDatabase = F2HDatabase.getInstance(this)
        var userSessionData: SessionEntity

        GlobalScope.launch {
            userSessionData = f2hDatabase.sessionDatabaseDao.getAll().get(0)
            drawerLayout.navView.getHeaderView(0).navHeaderProfileCredentials.text =
                userSessionData.userName + "\n" + userSessionData.address + "\n" + userSessionData.mobile

            drawerLayout.navView.getHeaderView(0).navHeaderGroupCredentials.text =
                userSessionData.groupName + "\n" + userSessionData.groupDescription
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.userPagesNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}
