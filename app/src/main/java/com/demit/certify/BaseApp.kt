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
            MicroblinkSDK.setLicenseKey("sRwAAAARY29tLmRlbWl0LmNlcnRpZnkMwIfX99g/hrAEnqh4k7AUOV4rQB1Bm35N9M1EKdsWVefpjAPo9CMy0DHcXlhvj/37a2Ra07uCQBepUSD3d9qC5UoqCbx5l+eDFaGrmZAgsYPEPS08qvFQSFYWSzz+kicuTxAX+S58HfM6M7lxCYUIGcbwebe4H82/MZCYprhSEH4FUoELOMA32frOC66X/Eyy9V5kh9fFKsxb34r/vyAAIFspmDuDbJrBh/7xY1Rttw==",this)
            MicroblinkSDK.setIntentDataTransferMode(IntentDataTransferMode.PERSISTED_OPTIMISED)
        }catch(ex: InvalidLicenceKeyException){
            ex.printStackTrace()
        }
    }
}