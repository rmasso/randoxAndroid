package com.demit.certify.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demit.certify.Models.TProfileModel
import com.demit.certify.R
import com.demit.certify.databinding.FragmentCertificatetypeBinding
import kotlinx.android.synthetic.main.fragment_certificatetype.*

class CertificateTypeFragment(val userProfile:TProfileModel) : Fragment(),View.OnClickListener {
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
    }

    override fun onResume() {
        super.onResume()
        if(binding.fitToTravelSwitch.isChecked)
            binding.btnScan.visibility=View.VISIBLE
        else if(binding.daysSwitch.isChecked)
            binding.btnFillForm.visibility=View.VISIBLE

    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.fit_to_travel_switch->{
                if(binding.fitToTravelSwitch.isChecked){
                    if(binding.daysSwitch.isChecked) {
                        binding.daysSwitch.isChecked = false
                        binding.btnFillForm.visibility= View.GONE
                    }
                    binding.btnScan.visibility=View.VISIBLE
                }else{
                    binding.btnScan.visibility=View.GONE
                }

            }
            R.id.days_switch->{
                if(binding.daysSwitch.isChecked){
                    if(binding.fitToTravelSwitch.isChecked) {
                        binding.fitToTravelSwitch.isChecked = false
                        binding.btnScan.visibility= View.GONE
                    }
                    binding.btnFillForm.visibility=View.VISIBLE
                }else{
                    binding.btnFillForm.visibility=View.GONE
                }

            }
            R.id.btn_cancel->{
                requireActivity().onBackPressed()
            }
            R.id.btn_scan->{
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragcontainer,ScancompleteFragment(userProfile,null)).addToBackStack("")
                    .commit()
            }
            R.id.btn_fill_form->{

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragcontainer,DataCaptureFragment(userProfile)).addToBackStack("")
                    .commit()
            }
        }

    }

    private fun attachClickListener(){
        with(binding){
            fitToTravelSwitch.setOnClickListener(this@CertificateTypeFragment)
            daysSwitch.setOnClickListener(this@CertificateTypeFragment)
            btnCancel.setOnClickListener(this@CertificateTypeFragment)
            btnScan.setOnClickListener(this@CertificateTypeFragment)
            btnFillForm.setOnClickListener(this@CertificateTypeFragment)
        }
    }

}