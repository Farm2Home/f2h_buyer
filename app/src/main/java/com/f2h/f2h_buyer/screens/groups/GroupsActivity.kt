package com.f2h.f2h_buyer.screens.groups

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.databinding.ActivityGroupsBinding
import com.f2h.f2h_buyer.screens.UserPagesActivity

class GroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get data from the login screen
        val message = intent.getStringExtra("Message_Key")
        println(message)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_groups)

        binding.button.setOnClickListener {
            goToMenuPage(it)
        }

    }

    private fun goToMenuPage(view: View?) {
        val intent = Intent(applicationContext, UserPagesActivity::class.java).apply {
            putExtra("Message_Key", "value in key")
        }
        startActivity(intent)
    }
}
