package com.demit.certify

import android.app.Application
import android.graphics.Bitmap
//import com.microblink.*
//import com.microblink.intent.IntentDataTransferMode
//import com.microblink.licence.exception.InvalidLicenceKeyException
//import com.zynksoftware.documentscanner.ui.DocumentScanner

class BaseApp: Application() {
    override fun onCreate() {
        super.onCreate()
//        val configuration = DocumentScanner.Configuration()
//        configuration.imageQuality = 100
//        configuration.imageType = Bitmap.CompressFormat.JPEG
//        DocumentScanner.init(this, configuration) //
//        try {
//            MicroblinkSDK.setLicenseFile("license.key",this)
//            MicroblinkSDK.setIntentDataTransferMode(IntentDataTransferMode.PERSISTED_OPTIMISED)
//        }catch(ex: InvalidLicenceKeyException){
//            ex.printStackTrace()
//        }
    }
}