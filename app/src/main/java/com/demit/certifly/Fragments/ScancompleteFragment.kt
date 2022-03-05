package com.demit.certifly.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.demit.certifly.Activities.CaptureActivity
import com.demit.certifly.Extras.DetachableClickListener
import com.demit.certifly.Extras.Global
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Models.CertificateModel
import com.demit.certifly.Models.TProfileModel
//import com.demit.certify.Activities.DeviceScanActivity
import com.demit.certifly.R
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.FragmentScancompleteBinding
import com.demit.certifly.extensions.toBase64String
import java.util.*


class ScancompleteFragment(
    val selectedProfile: TProfileModel,
    val additionalData: Map<String, String>?,
    val swabDateTime: String,
    val plfCode: String?
) : Fragment() {
    val SCAN_RESULT = 150
    lateinit var binding: FragmentScancompleteBinding
    lateinit var certificateModel: CertificateModel
    lateinit var sweet: Sweet
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScancompleteBinding.inflate(layoutInflater)
        sweet = Sweet(requireContext())
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
                        //val imageBytes = Base64.decode(image, Base64.DEFAULT)
                       // val bitmap= BitmapFactory.decodeFile(image)
                        Glide.with(requireContext())
                            .asBitmap()
                            .load(image)
                            .into(object :CustomTarget<Bitmap>(){
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    binding.scannedImage.setImageBitmap(resource)

                                    val base64Image= resource.toBase64String()
                                    certificateModel = createNewCertificate(base64Image, qrCode!!)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                }

                            })

                    }

                }

            }
        }

    }

    fun setclicks() {
        binding.submit.setOnClickListener {

            if (this::certificateModel.isInitialized) {
                sweet.show("Please Wait")
                submitCertificate(certificateModel)
            } else {
                Toast.makeText(requireContext(), "Please scan device", Toast.LENGTH_SHORT).show()
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
            cert_name = "${selectedProfile.usr_firstname} ${selectedProfile.usr_surname}",
            cert_email = selectedProfile.email,
            cert_passport = selectedProfile.usr_passport,
            cert_country = selectedProfile.usr_country,
            cert_device_id = qrCode,
            cert_ai_pred = "AI Predicate",
            cert_ai_approved = "N",
            cert_create = "",
            cert_deviceToken = qrCode,
            cert_image = deviceImageBase64
        )
        additionalData?.let { dataMap ->
            with(certificateModel) {
                cert_nationality = dataMap["cert_nationality"]!!
                is_viccinated = dataMap["is_viccinated"]!!
                vaccine_name = dataMap["vaccine_name"]!!
                is_fully_vaccinated_14days_uk = dataMap["is_fully_vaccinated_14days_uk"]!!
                pfl_code = dataMap["two_day_booking_ref"]!!
                transport_type = dataMap["transport_type"]!!
                isolation_address_line1 = dataMap["isolation_address_line1"]!!
                isolation_address_line2 = dataMap["isolation_address_line2"]!!
                town = dataMap["town"]!!
                post_code = dataMap["post_code"]!!
                arrival_date = dataMap["arrival_date"]!!
                fligh_vessel_train_no = dataMap["fligh_vessel_train_no"]!!
                nhs_no = dataMap["nhs_no"]!!
                country_territory_part_journey = dataMap["country_territory_part_journey"]!!
                last_date_department = dataMap["last_date_department"]!!
                Country_of_departure = dataMap["Country_of_departure"]!!
            }
        }
        certificateModel.swap_timestamp = swabDateTime
        plfCode?.let {
            certificateModel.pfl_code = plfCode
        }


        return certificateModel
    }

    private fun submitCertificate(certificateModel: CertificateModel) {

        ApiHelper.isQrValid(certificateModel.token, certificateModel.cert_device_id)
            .observe(viewLifecycleOwner) { validityResponse ->
                if (validityResponse == "0") {
                    ApiHelper.createNewCertificate(certificateModel)
                        .observe(viewLifecycleOwner) { response ->
                            // Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                            Log.d("++res++", response)
                            sweet.dismiss()
                            if (response != "Something went wrong" && !response.contains("100"))
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragcontainer, ScansubmitFragment())
                                    ?.addToBackStack("")?.commit()
                            else {
                                showAlertDialog()
                            }

                        }


                } else {
                    sweet.dismiss()
                    if (validityResponse == "100")
                        Toast.makeText(
                            requireContext(),
                            "Duplicate Test, please scan a new device",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(requireContext(), validityResponse, Toast.LENGTH_SHORT)
                            .show()

                }
            }
    }

    private fun showAlertDialog() {

        val positiveClickListener = DetachableClickListener.wrap { dialog, _ ->
            Global.should_go_home = true
            requireActivity().onBackPressed()
        }


        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Error")
            .setMessage("Submission failed.")
            .setPositiveButton("Ok", positiveClickListener)
            .setCancelable(false)
            .create()

        //avoid memory leaks
        positiveClickListener.clearOnDetach(dialog)

        dialog.show()
    }
}