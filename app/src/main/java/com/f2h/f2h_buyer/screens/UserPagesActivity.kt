package com.f2h.f2h_buyer.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.databinding.ActivityUserPagesBinding

class UserPagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPagesBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get data from the login screen
        val message = intent.getStringExtra("Message_Key")
        println(message)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_pages)
        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.userPagesNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.userPagesNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }



}
