package com.demit.certifly.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.demit.certifly.Extras.*
import com.demit.certifly.Extras.PermissionUtil.STORAGE_PERMISSION
import com.demit.certifly.Extras.PermissionUtil.hasStoragePermission
import com.demit.certifly.Fragments.PermissionInfoDialog
import com.demit.certifly.Models.AllCertificatesModel
import com.demit.certifly.R
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
        if (PermissionUtil.isMarshMallowOrAbove()) {
            storageRequestLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        getSharedPreferences("app", MODE_PRIVATE).edit()
                            .putBoolean("should_take_to_storage_settings", false).apply()
                        if (this::pdfBytesArray.isInitialized) {
                            FileUtil.saveFile(
                                this,
                                certificate.cert_device_id.takeLast(13),
                                pdfBytesArray
                            )
                        }
                    } else {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            if (!shouldShowRequestPermissionRationale(STORAGE_PERMISSION)) {
                                enable = true
                                getSharedPreferences("app", MODE_PRIVATE).edit()
                                    .putBoolean("should_take_to_storage_settings", true).apply()
                            }

                        } else {
                            //Android 11 and up
                            /*enable = true
                            getSharedPreferences("app", MODE_PRIVATE).edit()
                                .putBoolean("should_take_to_storage_settings", true).apply()*/
                        }

                    }

                }
        }

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (this::pdfBytesArray.isInitialized) {
                FileUtil.saveFile(this, certificate.cert_device_id.takeLast(13), pdfBytesArray)
            }
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        val hasWritePermission = hasStoragePermission(this)
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val writePermissionGranted = hasWritePermission || minSdk29
        when {

            writePermissionGranted -> {
                if (this::pdfBytesArray.isInitialized) {
                    FileUtil.saveFile(this, certificate.cert_device_id.takeLast(13), pdfBytesArray)
                }
            }
            enable -> {
                val permissionSettingsDialog =
                    PermissionInfoDialog(
                        true,
                        Constants.DIALOG_TYPE_STORAGE
                    ) { btnClickId, dialog ->
                        when (btnClickId) {
                            R.id.btn_settings -> {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                val uri = Uri.fromParts("package", packageName, null);
                                intent.data = uri;
                                startActivityForResult(intent, 1002)
                                dialog.dismiss()
                            }
                            R.id.btn_not_now -> {
                                dialog.dismiss()
                            }
                        }
                    }
                permissionSettingsDialog.isCancelable = false
                permissionSettingsDialog.show(supportFragmentManager, "permission_dialog")


            }
            else -> {
                //Show some cool ui to the user explaining why we use this permission
                val permissionDialog =
                    PermissionInfoDialog(
                        false,
                        Constants.DIALOG_TYPE_STORAGE
                    ) { btnClickId, dialog ->
                        when (btnClickId) {
                            R.id.btn_allow -> {
                                storageRequestLauncher.launch(STORAGE_PERMISSION)
                                dialog.dismiss()
                            }
                            R.id.btn_not_now -> {
                                dialog.dismiss()
                            }
                        }
                    }
                permissionDialog.isCancelable = false
                permissionDialog.show(supportFragmentManager, "permission_dialog")

            }
        }
    }


}