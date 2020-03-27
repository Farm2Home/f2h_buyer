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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_groups)
        binding.button.setOnClickListener {
            goToMenuPage(it)
        }

    }

    private fun goToMenuPage(view: View?) {
        val intent = Intent(applicationContext, UserPagesActivity::class.java)
        startActivity(intent)
    }
}
