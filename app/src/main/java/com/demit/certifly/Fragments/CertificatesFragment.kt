package com.demit.certifly.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.demit.certifly.Extras.Constants
import com.demit.certifly.Extras.Functions
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Interfaces.DialogDismissInterface
import com.demit.certifly.Models.AllCertificatesModel
import com.demit.certifly.adapters.CertificateAdapter
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.FragmentCertificatesBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_certificates.*
import org.json.JSONObject

class CertificatesFragment : Fragment(), DialogDismissInterface {
    lateinit var sweet: Sweet
    lateinit var binding: FragmentCertificatesBinding
    lateinit var certificatesList: MutableList<AllCertificatesModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCertificatesBinding.inflate(layoutInflater)


        sweet = Sweet(requireContext());
        fetchCertificates()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this::sweet.isInitialized)
            sweet.dismiss()
    }

    override fun dismissDialog() {
        sweet.dismiss()
        cardStack.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun fetchCertificates(){
        sweet.show("Retrieving Certificates")
        if (binding.noCertificateMessage.visibility == View.VISIBLE)
            binding.noCertificateMessage.visibility = View.GONE

        ApiHelper.fetchCertificates(Shared(requireContext()).getString("token")).observe(viewLifecycleOwner){ response->
            if(response.count!=-1){
                binding.pendingCount.text = "Pending: ${response.count}"
                binding.pendingCount.visibility = View.VISIBLE
                response.certificates?.let {
                    if (it.isNotEmpty()) {
                        binding.cardStack.adapter = CertificateAdapter(
                            requireActivity(),
                            it,
                            this@CertificatesFragment
                        ){ deleteCertId ->
                            deleteCertificate(deleteCertId)
                        }
                    } else {
                        binding.noCertificateMessage.visibility = View.VISIBLE

                    }
                    sweet.dismiss()
                }


            }else{
                Toast.makeText(requireContext(), response.message,Toast.LENGTH_LONG).show()
                sweet.dismiss()
            }
        }
    }

    private fun deleteCertificate(certId: String) {
        sweet.show("Deleting Certificate")
        ApiHelper.deleteCertificate(Shared(requireContext()).getString("token"), certId)
            .observe(viewLifecycleOwner, { response ->
                if(response.success){
                    sweet.dismiss()
                    binding.cardStack.removeAdapter()
                    fetchCertificates()
                }else{
                    sweet.dismiss()
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


}