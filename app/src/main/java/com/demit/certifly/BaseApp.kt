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
            MicroblinkSDK.setLicenseKey("sRwAAAASY29tLmRlbWl0LmNlcnRpZmx5WpCGl43EjZKT6DLykIEgnyUnm1dXpKRTW/S/FKaOXVR8tTQe9c/PheGGnw5KevLw5hb2u4ARGVHFwwxxzJ8iYXLPvWYxQii7lhgkysB+6uGanXNV3ScRA3Xs5o2gay626xr9kYljuTujxB8TdQ6Q6moOM6u0bUaw7kp4hZTtxOqZpE8p7GqoZ/fZBMzT3jhiT1Kzk7FIq2SvyND/dNa0HI9onRZ+QqzQqOMKTA9pxbk=",this)
            MicroblinkSDK.setIntentDataTransferMode(IntentDataTransferMode.PERSISTED_OPTIMISED)
        }catch(ex: InvalidLicenceKeyException){
            ex.printStackTrace()
        }
    }
}