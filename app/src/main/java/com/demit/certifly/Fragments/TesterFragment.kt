package com.demit.certifly.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Cache
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.demit.certifly.Extras.Constants
import com.demit.certifly.Extras.Functions
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.databinding.FragmentTesterBinding
import com.demit.certifly.databinding.ViewTesterBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class TesterFragment : Fragment() {
    var list : MutableList<TProfileModel> = ArrayList();
    lateinit var binding : FragmentTesterBinding
    lateinit var adapter : TesterAdapter
    lateinit var currentSelectedProfile:TProfileModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTesterBinding.inflate(layoutInflater)
        sweet = Sweet(requireContext())
        binding.tlist.layoutManager = LinearLayoutManager(context)
        adapter = TesterAdapter()
        binding.tlist.adapter = adapter
        setclicks()
        return binding.root
    }

    fun setclicks(){
        binding.gotop.setOnClickListener {
            /*activity?.supportFragmentManager?.beginTransaction()
               ?.replace(R.id.fragcontainer,ScancompleteFragment(currentSelectedProfile,null))?.addToBackStack("")?.commit()
*/
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragcontainer,CertificateTypeFragment(currentSelectedProfile))?.addToBackStack("")?.commit()
        }
        binding.cancel.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.newprofile.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragcontainer,ProfileFragment())?.addToBackStack("")?.commit()
        }
    }
    fun setData(positon : Int){
        for(i in 0 until list.size){
            list[i].checked = false
        }
        list[positon].checked = true
        currentSelectedProfile= list[positon]
        binding.gotop.visibility = View.VISIBLE
        adapter.notifyDataSetChanged()
    }

    inner class TesterAdapter : RecyclerView.Adapter<TesterAdapter.TesterVH>() {
        lateinit var context : Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TesterVH {
            val inflater = LayoutInflater.from(parent.context)
            val binding : ViewTesterBinding = ViewTesterBinding.inflate(inflater,parent,false)
            return  TesterVH(binding);
        }

        override fun onBindViewHolder(holder: TesterVH, position: Int) {
            val model = this@TesterFragment.list[position];
            holder.itemView.setOnClickListener{
                if(!model.checked) {
                    this@TesterFragment.setData(position)
                }
            }
            holder.binding.rd.setOnClickListener {
                if(!model.checked) {
                    this@TesterFragment.setData(position)
                }
            }
            holder.binding.tname.text = model.usr_firstname + " " + model.usr_surname

            holder.binding.rd.isChecked = model.checked
        }

        override fun getItemCount(): Int {
            return this@TesterFragment.list.size
        }

        inner class TesterVH(val binding: ViewTesterBinding) : RecyclerView.ViewHolder(binding.root)
    }

    override fun onStart() {
        binding.gotop.visibility = View.GONE
        for(i in 0 until list.size){
            if(list[0].checked){
                binding.gotop.visibility = View.VISIBLE
            }
        }
        getProfiles()
        super.onStart()
    }
    lateinit var sweet : Sweet
    fun getProfiles(){
        sweet.show("Getting profiles")

        val url = Functions.concat(Constants.url , "getProfile.php");
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
                        val listType = object : TypeToken<List<TProfileModel?>?>() {}.type
                        val sliderItem: MutableList<TProfileModel> = gson.fromJson(obj.get("ret").toString(), listType)
                        list = sliderItem
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
                if(context != null) {
                    map["token"] = Shared(context!!).getString("token")!!
                }
                return map
            }

            override fun getCacheEntry(): Cache.Entry? {
                return super.getCacheEntry()
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
        )
        if(context != null) {
            val queue = Volley.newRequestQueue(context)
            queue.cache.clear()
            queue.add(request)
        }
    }
}