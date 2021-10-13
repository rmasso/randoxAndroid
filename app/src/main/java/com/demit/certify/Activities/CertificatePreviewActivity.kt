package com.demit.certify.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.demit.certify.Extras.CertificateGenerator
import com.demit.certify.Extras.FileUtil
import com.demit.certify.Extras.Sweet
import com.demit.certify.Models.AllCertificatesModel
import com.demit.certify.R
import com.demit.certify.databinding.ActivityCertificatePreviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class CertificatePreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificatePreviewBinding
    private lateinit var certificate: AllCertificatesModel
    private lateinit var pdfBytesArray: ByteArray
    private lateinit var sweet: Sweet
    private lateinit var permissionResultLauncher: ActivityResultLauncher<String>
    private var writePermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificatePreviewBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.saveBtn.setOnClickListener { //Save the file
            saveFile()
        }
        certificate = intent.getSerializableExtra("certificate") as AllCertificatesModel
        sweet = Sweet(this)
        sweet.show("Loading Please Wait")
        generatePdfForPreview()

        permissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()){ isWritePermissionGranted->
                if(isWritePermissionGranted){
                    if (this::pdfBytesArray.isInitialized) {
                        FileUtil.saveFile(this,certificate.cert_device_id.takeLast(13),pdfBytesArray)
                    }
                }else{
                    Toast.makeText(this,"Can't Save file because permission denied",Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun generatePdfForPreview() {
        lifecycleScope.launch(Dispatchers.IO) {
            val pdfDeferred = async {
                CertificateGenerator.generateCertificate(
                    this@CertificatePreviewActivity,
                    certificate
                )
            }
            val pdfBaos = pdfDeferred.await()

            pdfBaos?.let {
                pdfBytesArray = pdfBaos.toByteArray()
                Handler(Looper.getMainLooper()).postDelayed({
                    runOnUiThread {
                        binding.pdfView.fromBytes(pdfBytesArray).load()
                        sweet.dismiss()
                    }
                }, 500)

            }


        }
    }

    private fun saveFile() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            if (this::pdfBytesArray.isInitialized) {
                FileUtil.saveFile(this,certificate.cert_device_id.takeLast(13),pdfBytesArray)
            }
        }else{
            requestPermission()
        }
    }


    fun requestPermission() {

        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionResultLauncher.launch(permissionsToRequest[0])
        }else{
            if (this::pdfBytesArray.isInitialized) {
                FileUtil.saveFile(this,certificate.cert_device_id.takeLast(13),pdfBytesArray)
            }
        }

    }


}