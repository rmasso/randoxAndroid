package com.demit.certify.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demit.certify.R
import com.demit.certify.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding :ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signin.setOnClickListener{
            startActivity(Intent(this,DashboardActivity::class.java))
        }
    }
}