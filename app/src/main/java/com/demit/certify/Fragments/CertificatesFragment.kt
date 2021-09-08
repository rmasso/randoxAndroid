package com.demit.certify.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Functions
import com.demit.certify.Extras.Sweet
import com.demit.certify.Models.AllCertificatesModel
import com.demit.certify.R
import com.demit.certify.databinding.FragmentCertificatesBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.ArrayList

class CertificatesFragment : Fragment() {
    lateinit var sweet : Sweet
    lateinit var binding: FragmentCertificatesBinding
    lateinit var adapter : CertificateAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCertificatesBinding.inflate(layoutInflater)
        adapter = CertificateAdapter(requireContext(), 0)
        binding.stackview.adapter = adapter

        sweet = Sweet(requireContext());
        getCertificates()
        return binding.root
    }

    var l: MutableList<AllCertificatesModel> = ArrayList()
    inner class CertificateAdapter(context: Context, resource: Int) : ArrayAdapter<Any?>(context, resource) {

        override fun getCount(): Int {
            return l.size
        }

        override fun getItem(position: Int): Any? {
            return l[position]
        }

        override fun getView(position: Int, convertview: View?, parent: ViewGroup): View {
            var convertView = convertview
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_certificates, parent, false)
            }
            val model = l[position]
            val name : TextView = convertView!!.findViewById(R.id.name)
            val dob : TextView = convertView.findViewById(R.id.dob)
            val tdate : TextView = convertView.findViewById(R.id.tdate)
            val passport : TextView = convertView.findViewById(R.id.passport)

            name.text = model.cert_name
            passport.text = model.cert_passport
            tdate.text = model.cert_create
//            dob.text = model.cert_name
            return convertView
        }
    }


    fun getCertificates(){
        sweet.show("Loging in")

        val url = Functions.concat(Constants.url , "getAdminAllCert.php");
        val request : StringRequest = object : StringRequest(
            Method.POST,
            url,

            Response.Listener{
                sweet.dismiss()
                Log.d("sss" , it.toString())

                try{
                    val obj = JSONObject(it)
                    val s = obj.getString("ret");
                    if(s == "100"){
                        if(context != null) {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }else{
                        val gson = Gson()
                        val listType = object : TypeToken<List<AllCertificatesModel?>?>() {}.type
                        val sliderItem: MutableList<AllCertificatesModel> = gson.fromJson(obj.get("ret").toString(), listType)
                        l = sliderItem
                        adapter.notifyDataSetChanged()

                    }
                }catch (e : Exception){
                    e.printStackTrace()
                    sweet.dismiss()
                    if(context != null) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
            Response.ErrorListener{
                sweet.dismiss()
                if(context != null) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val map : MutableMap<String, String> = HashMap();
                map["token"] = Constants.adminToken
                return map
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
        )
        if(context != null) {
            Volley.newRequestQueue(context).add(request)
        }
    }
}