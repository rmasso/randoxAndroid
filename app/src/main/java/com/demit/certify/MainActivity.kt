package com.demit.certify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.demit.certify.Activities.DashboardActivity
import com.demit.certify.Activities.LoginActivity
import com.demit.certify.Extras.Shared

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Handler().postDelayed({

            if(Shared(this).getString("token") == ""){
                startActivity(Intent(this, LoginActivity::class.java))
            }else{
                startActivity(Intent(this, DashboardActivity::class.java))
            }

            finish()
        }, 3000)

    }
}