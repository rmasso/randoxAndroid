package com.demit.certifly.Fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.FragmentCertificatetypeBinding
import kotlinx.android.synthetic.main.fragment_certificatetype.*

class CertificateTypeFragment(val userProfile: TProfileModel) : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentCertificatetypeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_certificatetype,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachClickListener()

        checkTestAvailabilityStatus()

        binding.daysSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (binding.fitToTravelSwitch.isChecked) {
                    binding.fitToTravelSwitch.isChecked = false
                    binding.btnPurchase.visibility = View.GONE
                }
                binding.btnFillForm.visibility = View.VISIBLE
            } else {
                binding.btnFillForm.visibility = View.GONE
            }
        }

        binding.fitToTravelSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (binding.daysSwitch.isChecked) {
                    binding.daysSwitch.isChecked = false
                    binding.btnFillForm.visibility = View.GONE
                }
                binding.btnPurchase.visibility = View.VISIBLE
            } else {
                binding.btnPurchase.visibility = View.GONE
            }
        }
        binding.pcrLink.movementMethod = LinkMovementMethod()

    }

    override fun onResume() {
        super.onResume()
        if (binding.fitToTravelSwitch.isChecked)
            binding.btnPurchase.visibility = View.VISIBLE
        else if (binding.daysSwitch.isChecked)
            binding.btnFillForm.visibility = View.VISIBLE

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_cancel -> {
                requireActivity().onBackPressed()
            }
            R.id.btn_purchase -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragcontainer, PlfFitFragment(userProfile)).addToBackStack("")
                    .commit()
            }
            R.id.btn_fill_form -> {

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragcontainer, DataCaptureFragment(userProfile))
                    .addToBackStack("")
                    .commit()
            }
        }

    }

    private fun attachClickListener() {
        with(binding) {
            fitToTravelSwitch.setOnClickListener(this@CertificateTypeFragment)
            daysSwitch.setOnClickListener(this@CertificateTypeFragment)
            btnCancel.setOnClickListener(this@CertificateTypeFragment)
            btnPurchase.setOnClickListener(this@CertificateTypeFragment)
            btnFillForm.setOnClickListener(this@CertificateTypeFragment)
        }
    }

    private fun checkTestAvailabilityStatus() {
        val sweet = Sweet(requireContext())
        sweet.show("Please Wait")
        ApiHelper.getTestAvailability().observe(viewLifecycleOwner, {
            it?.let { typeStatusList ->
                typeStatusList.forEach { typeStatus ->
                    if (typeStatus.type == "fit" && typeStatus.status == "1") {
                        binding.fitToTravelSwitch.isEnabled = true
                        binding.fitToTravelMessage.visibility = View.GONE
                    } else if (typeStatus.type == "day2" && typeStatus.status == "1") {
                        binding.daysSwitch.isEnabled = true
                        binding.day2Message.visibility = View.GONE
                    }
                }
                sweet.dismiss()
            } ?: run {
                sweet.dismiss()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

}