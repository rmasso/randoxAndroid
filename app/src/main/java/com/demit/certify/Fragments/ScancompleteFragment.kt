package com.demit.certify.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.demit.certify.Activities.CaptureActivity
import com.demit.certify.Extras.Shared
import com.demit.certify.Extras.Sweet
import com.demit.certify.Models.CertificateModel
import com.demit.certify.Models.TProfileModel
//import com.demit.certify.Activities.DeviceScanActivity
import com.demit.certify.R
import com.demit.certify.data.ApiHelper
import com.demit.certify.databinding.FragmentScancompleteBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ScancompleteFragment(val selectedProfile: TProfileModel) : Fragment() {
    val SCAN_RESULT = 150
    lateinit var binding: FragmentScancompleteBinding
    lateinit var certificateModel:CertificateModel
    lateinit var sweet:Sweet
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScancompleteBinding.inflate(layoutInflater)
        sweet= Sweet(requireContext())
        setclicks()
        startScanning()
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_RESULT) {
            if (resultCode != Activity.RESULT_CANCELED) {
                //Todo add image initilization related code here
                data?.let {
                    val croppedImage = it.getStringExtra("device")
                    val qrCode = it.getStringExtra("qr")
                    croppedImage?.let { image ->
                        val imageBytes = Base64.decode(image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        binding.scannedImage.setImageBitmap(bitmap)
                      certificateModel=  createNewCertificate(image, qrCode!!)
                    }

                }

            }
        }

    }

    fun setclicks() {
        binding.submit.setOnClickListener {

            if(this::certificateModel.isInitialized){
                sweet.show("Please Wait...")
                submitCertificate(certificateModel)
            }
        }
        binding.rescan.setOnClickListener {
            startScanning()
        }
    }

    private fun startScanning() {
        val intent = Intent(requireContext(), CaptureActivity::class.java)
        startActivityForResult(intent, SCAN_RESULT)
    }

    private fun createNewCertificate(deviceImageBase64: String, qrCode: String): CertificateModel {
        val token = Shared(requireContext()).getString("token")

        val certificateModel = CertificateModel(
            token = token,
            usr_id = selectedProfile.usr_id,
            cert_name = selectedProfile.usr_firstname + " Covid Cert",
            cert_email = selectedProfile.email,
            cert_passport = selectedProfile.usr_passport,
            cert_country = selectedProfile.usr_country,
            cert_device_id = qrCode,
            cert_ai_pred = "AI Predicate",
            cert_ai_approved = "N",
            cert_create = SimpleDateFormat("dd-MMM-yyyy hh:mm").format(Date()),
            cert_deviceToken = qrCode,
            cert_image = deviceImageBase64
        )
        return certificateModel
    }

    private fun submitCertificate(certificateModel:CertificateModel){
        ApiHelper.isQrValid(certificateModel.token, certificateModel.cert_device_id).observe(viewLifecycleOwner) { validityResponse ->
            if (validityResponse == "0") {

                ApiHelper.createNewCertificate(certificateModel)
                    .observe(viewLifecycleOwner) { response ->
                        // Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                        Log.d("++res++",response)
                        sweet.dismiss()
                        if(response!="Something went wrong")
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragcontainer, ScansubmitFragment())?.addToBackStack("")?.commit()
                        else{
                            Toast.makeText(requireContext(), validityResponse, Toast.LENGTH_SHORT).show()
                        }

                    }


            } else {
                sweet.dismiss()
                if (validityResponse == "100")
                    Toast.makeText(requireContext(), "Bad Device Can't Submit", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), validityResponse, Toast.LENGTH_SHORT).show()

            }
        }
    }
}