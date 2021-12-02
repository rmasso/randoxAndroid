package com.demit.certifly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.demit.certifly.Activities.LoginActivity
import com.demit.certifly.Activities.PrivacyActivity
import com.demit.certifly.Extras.Shared

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Handler().postDelayed({

            if (Shared(this@MainActivity).getString("is_first_time") == "yes")
                startActivity(Intent(this, LoginActivity::class.java))
            else
                startActivity(Intent(this, PrivacyActivity::class.java))


            finish()
        }, 3000)

    }
}