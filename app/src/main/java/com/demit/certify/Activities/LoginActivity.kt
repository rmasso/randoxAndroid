package com.demit.certify.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.demit.certify.R
import com.demit.certify.databinding.ActivityLoginBinding
import java.util.jar.Manifest

class LoginActivity : AppCompatActivity() {
    lateinit var binding :ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clicks()
    }

    private fun clicks() {
        binding.signin.setOnClickListener{
            startActivity(Intent(this,DashboardActivity::class.java))
        }
        binding.scan.setOnClickListener {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                startActivity(Intent(this,ScanActivity::class.java))
                return@setOnClickListener
            }
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(this,ScanActivity::class.java))
            }else{
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),1)
            }

        }
    }

}