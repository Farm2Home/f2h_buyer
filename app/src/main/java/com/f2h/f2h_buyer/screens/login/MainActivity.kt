package com.f2h.f2h_buyer.screens.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.databinding.ActivityMainBinding
import com.f2h.f2h_buyer.network.LoginApi
import com.f2h.f2h_buyer.screens.groups.GroupsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.loginButton.setOnClickListener {
            tryLogin(it)
        }

    }

    private fun tryLogin(view: View) {
        binding.progressBar.visibility = View.VISIBLE;
        val mobile: String = binding.mobileNumber.text.toString()
        val password: String = binding.password.text.toString()


        // check login enabled or not nad move to next page
        var loginEnabled: Boolean = false;
        if (!loginEnabled){
            val intent = Intent(applicationContext, GroupsActivity::class.java).apply {
                putExtra("user_id", "1")
            }
            startActivity(intent)
        }

        LoginApi.retrofitService.getUserDetails(mobile, password).enqueue(object: Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                println("Failed : " + t.message )
                binding.progressBar.visibility = View.GONE;
                Toast.makeText(applicationContext,"Login Failed - Network issue",Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                println("Success : " + response.body())
                binding.progressBar.visibility = View.GONE;

                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(applicationContext, GroupsActivity::class.java).apply {
                        putExtra("user_id", "1")
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext,"Login Failed - Wrong Credentials",Toast.LENGTH_SHORT).show()
                }
            }

        })

    }
}
