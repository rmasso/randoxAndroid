package com.demit.certify

import android.app.Application
import com.microblink.MicroblinkSDK
import com.microblink.intent.IntentDataTransferMode
import com.microblink.licence.exception.InvalidLicenceKeyException

class BaseApp: Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            MicroblinkSDK.setLicenseFile("license.key",this)
            MicroblinkSDK.setIntentDataTransferMode(IntentDataTransferMode.PERSISTED_OPTIMISED)
        }catch(ex: InvalidLicenceKeyException){
            ex.printStackTrace()
        }
    }
}