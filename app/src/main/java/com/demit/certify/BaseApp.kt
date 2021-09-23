package com.demit.certify

import android.app.Application
import android.graphics.Bitmap
import com.androidnetworking.AndroidNetworking
import com.microblink.MicroblinkSDK
import com.microblink.intent.IntentDataTransferMode
import com.microblink.licence.exception.InvalidLicenceKeyException
class BaseApp: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(applicationContext)
        try {
            MicroblinkSDK.setLicenseFile("license.key",this)
            MicroblinkSDK.setIntentDataTransferMode(IntentDataTransferMode.PERSISTED_OPTIMISED)
        }catch(ex: InvalidLicenceKeyException){
            ex.printStackTrace()
        }
    }
}