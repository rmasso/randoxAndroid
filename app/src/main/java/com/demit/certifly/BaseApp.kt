package com.demit.certifly

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.androidnetworking.AndroidNetworking
import com.microblink.MicroblinkSDK
import com.microblink.intent.IntentDataTransferMode
import com.microblink.licence.exception.InvalidLicenceKeyException
class BaseApp: Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        AndroidNetworking.initialize(applicationContext)
        try {
            MicroblinkSDK.setLicenseKey("sRwAAAASY29tLmRlbWl0LmNlcnRpZmx5WpCGl43EjZKT6DJykFngVGgZjjRMwxBGDVj33BqUE0bYr0maNuIAHkzGi2Q5gqSWfAHk4iE63BoVWYZjSoCfkVoh8w1IrMJFpLsiJhbY2xwdE9YGJxvhElgpWZKHRkeF+wTGTJVHeYYa8PXLBN3gGKcFzc/0Kal0RlaVdjaZs2F9UjW81f5VNa2qyFN4C6XCs2sqBH6eHWXIpqvlkPrswDNS1qkxBQ==",this)
            MicroblinkSDK.setIntentDataTransferMode(IntentDataTransferMode.PERSISTED_OPTIMISED)
        }catch(ex: InvalidLicenceKeyException){
            ex.printStackTrace()
        }
    }
}