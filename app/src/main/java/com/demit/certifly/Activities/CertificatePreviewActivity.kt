package com.demit.certifly.Activities

import android.content.Intent
import android.os.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.demit.certifly.Extras.*
import com.demit.certifly.Extras.PermissionUtil.hasStoragePermission
import com.demit.certifly.Models.AllCertificatesModel
import com.demit.certifly.databinding.ActivityCertificatePreviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CertificatePreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificatePreviewBinding
    private lateinit var certificate: AllCertificatesModel
    private lateinit var pdfBytesArray: ByteArray
    private lateinit var sweet: Sweet

    //Storage Permission Variables
    lateinit var storageRequestLauncher: ActivityResultLauncher<String>
    var enable = false
    private val STORAGE_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificatePreviewBinding.inflate(layoutInflater)

        setContentView(binding.root)

        enable = getSharedPreferences("app", MODE_PRIVATE).getBoolean(
            "should_take_to_storage_settings",
            false
        )

        binding.saveBtn.setOnClickListener { //Save the file
            saveFile()
        }
        certificate = intent.getSerializableExtra("certificate") as AllCertificatesModel
        sweet = Sweet(this)
        sweet.show("Loading Please Wait")
        generatePdfForPreview()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (hasStoragePermission(this)) {
                getSharedPreferences("app", MODE_PRIVATE).edit()
                    .putBoolean("should_take_to_storage_settings", false).apply()
                if (this::pdfBytesArray.isInitialized) {
                    FileUtil.saveFile(this, certificate.cert_device_id.takeLast(13), pdfBytesArray)
                }
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

        if (this::pdfBytesArray.isInitialized) {
            FileUtil.saveFile(this, certificate.cert_device_id.takeLast(13), pdfBytesArray)
        }
    }


}