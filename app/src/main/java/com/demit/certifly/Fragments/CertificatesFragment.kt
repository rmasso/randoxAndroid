package com.demit.certifly.Fragments

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
        getCertificates()
        return binding.root
    }

    override fun dismissDialog() {
        sweet.dismiss()
        cardStack.visibility = View.VISIBLE
    }

    fun getCertificates() {
        sweet.show("Retrieving Certificates")
        if (binding.noCertificateMessage.visibility == View.VISIBLE)
            binding.noCertificateMessage.visibility = View.GONE
        val url = Functions.concat(Constants.url, "getAllCert.php");
        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,

            Response.Listener {
                Log.d("sss", it.toString())

                try {
                    val obj = JSONObject(it)
                    val s = obj.getString("ret")
                    Log.d("++res++",s)
                    if (s == "100") {
                        if (context != null) {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        val pendingCount = obj.getString("count")
                        binding.pendingCount.text = "Pending: $pendingCount"
                        binding.pendingCount.visibility = View.VISIBLE

                        val gson = Gson()
                        val listType = object : TypeToken<List<AllCertificatesModel?>?>() {}.type
                        val sliderItem: MutableList<AllCertificatesModel> =
                            gson.fromJson(obj.get("ret").toString(), listType)
                        if (sliderItem.size > 0) {
                            binding.cardStack.adapter = CertificateAdapter(
                                requireActivity(),
                                sliderItem,
                                this@CertificatesFragment
                            )
                        } else {
                            binding.noCertificateMessage.visibility = View.VISIBLE
                            sweet.dismiss()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    sweet.dismiss()
                    if (context != null) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
            Response.ErrorListener {
                sweet.dismiss()
                if (context != null) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap();
                map["token"] = Shared(requireContext()).getString("token")
                return map
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
        )
        if (context != null) {
            Volley.newRequestQueue(context).add(request)
        }
    }


}