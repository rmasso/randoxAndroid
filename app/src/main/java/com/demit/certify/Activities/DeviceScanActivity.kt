//package com.demit.certify.Activities
//
//import android.content.Intent
//import android.os.Bundle
//import com.zynksoftware.documentscanner.ScanActivity
//import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
//import com.zynksoftware.documentscanner.model.ScannerResults
//import java.io.File
//
//class DeviceScanActivity : ScanActivity() {
//    val SCAN_RESULT=150
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        addFragmentContentLayout()
//    }
//
//    override fun onSuccess(scannerResults: ScannerResults) {
//        val intent= Intent()
//        intent.putExtra("original_image",scannerResults.originalImageFile)
//        intent.putExtra("cropped_image",scannerResults.croppedImageFile)
//        setResult(SCAN_RESULT,intent)
//        finish()
//    }
//
//
//    override fun onError(error: DocumentScannerErrorModel) {
//        finish()
//    }
//
//
//
//    override fun onClose() {
//       finish()
//    }
//}