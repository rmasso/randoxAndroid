package com.demit.certify.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.demit.certify.Extras.CertificateGenerator
import com.demit.certify.Extras.Sweet
import com.demit.certify.Models.AllCertificatesModel
import com.demit.certify.R
import com.demit.certify.databinding.ActivityCertificatePreviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CertificatePreviewActivity : AppCompatActivity() {

    lateinit var binding: ActivityCertificatePreviewBinding
    lateinit var certificate: AllCertificatesModel
    lateinit var pdfBytesArray: ByteArray
    lateinit var sweet: Sweet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificatePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.saveBtn.setOnClickListener {
            Toast.makeText(this,"Under Development",Toast.LENGTH_SHORT).show()
        }
        certificate = intent.getSerializableExtra("certificate") as AllCertificatesModel
        sweet = Sweet(this)
        sweet.show("Loading Please Wait")
        generatePdfForPreview()
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


}