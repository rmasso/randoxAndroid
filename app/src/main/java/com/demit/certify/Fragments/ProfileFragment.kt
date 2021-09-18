package com.demit.certify.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Cache
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Functions
import com.demit.certify.Extras.Shared
import com.demit.certify.Extras.Sweet
import com.demit.certify.Models.ProfileModel
import com.demit.certify.Models.TProfileModel
import com.demit.certify.databinding.FragmentProfileBinding
import com.demit.certify.databinding.ViewProfileBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.HashMap

//import com.microblink.entities.recognizers.Recognizer
//import com.microblink.entities.recognizers.RecognizerBundle
//import com.microblink.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer
//import com.microblink.uisettings.ActivityRunner
//import com.microblink.uisettings.BlinkIdUISettings


class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var adapter: ProfileAdapter
    var index = 0
    var list: MutableList<TProfileModel> = ArrayList();

    lateinit var sweet : Sweet
    //Blink Id Variables
//    private lateinit var mRecognizer: BlinkIdCombinedRecognizer
//    private lateinit var mRecognizerBundle: RecognizerBundle
    private val MY_REQUEST_CODE = 801
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        sweet = Sweet(requireContext())



        getProfiles();
        adapter = ProfileAdapter()
        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mRecognizer = BlinkIdCombinedRecognizer()
//        mRecognizer.setReturnFaceImage(true)
        // bundle recognizers into RecognizerBundle
//        mRecognizerBundle = RecognizerBundle(mRecognizer)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode === MY_REQUEST_CODE) {
//            if (resultCode === RESULT_OK && data != null) {
//                // load the data into all recognizers bundled within your RecognizerBundle
////                mRecognizerBundle.loadFromIntent(data)
//
//                // now every recognizer object that was bundled within RecognizerBundle
//                // has been updated with results obtained during scanning session
//                // you can get the result by invoking getResult on recognizer
////                val result = mRecognizer.result
//                // result is valid, you can use it however you wish
//                if (result.resultState == Recognizer.Result.State.Valid) {
//                    binding.fname.text= Editable.Factory.getInstance().newEditable( result.firstName)
//                    binding.sname.text= Editable.Factory.getInstance().newEditable( result.lastName)
//
//                    result.dateOfBirth.date?.let {date->
//                        val day= date.day
//                        val year= date.year
//                        val month= date.month
//                        binding.dob.text= Editable.Factory.getInstance().newEditable("$day/$month/$year")
//                    }
//
//
//                    binding.pnumber.text= Editable.Factory.getInstance().newEditable( result.documentNumber)
//
//
//
//
//                    /* val imageArr = result.encodedFaceImage
//                     faceImage.setImageBitmap(
//                         result.faceImage?.convertToBitmap()
//                     )*/
//
//               /*     val p = ProfileModel();
//                list.add(p)
//                binding.rv.getAdapter()?.itemCount?.minus(1)?.let { it1 ->
//                    binding.rv.smoothScrollToPosition(
//                        it1
//                    )
//                };
//                setData(list.size - 1)*/
//
//                }
//            }
//        }
//
//    }

    fun clicks() {
        binding.add.setOnClickListener() {
//            startScanning()
            val p = TProfileModel();
            list.add(p)
            binding.rv.getAdapter()?.itemCount?.minus(1)?.let { it1 ->
                binding.rv.smoothScrollToPosition(
                    it1
                )
            };
            setData(list.size - 1)
        }

        binding.save.setOnClickListener {
            if(binding.fname.text.toString().isEmpty()){
                if(context != null)
                Toast.makeText(context, "Firstname can't be empty",Toast.LENGTH_SHORT).show()
            }else if(binding.sname.text.toString().isEmpty()){
                if(context != null)
                Toast.makeText(context, "Surname can't be empty",Toast.LENGTH_SHORT).show()
            }else
                if(!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()){
                    if(context != null)
                Toast.makeText(context, "Invalid Email Address",Toast.LENGTH_SHORT).show()
                }else if(binding.dob.text.toString().isEmpty()){
                    if(context != null)
                    Toast.makeText(context, "Date of birth can't be empty",Toast.LENGTH_SHORT).show()
                }else{
                    familyregister()
                }
        }
    }

    fun fielddata() {
        binding.fname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_firstname = s.toString();
            }

        })

        binding.sname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_surname = s.toString();
            }

        })

        binding.dob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_birth = s.toString();
            }

        })

        binding.pnumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_passport = s.toString();
            }

        })

        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_email = s.toString();
            }

        })


        binding.phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_phone = s.toString();
            }

        })

        binding.address.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_home = s.toString();
            }

        })

        binding.zip.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_zip = s.toString();
            }
        })

        val p :String = if(binding.gender.selectedItemPosition == 0){
            "M"
        }else{
            "F"
        }

        list[index].usr_sex = p

        binding.gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position == 0){
                    list[index].usr_sex = "M"
                }else{
                    list[index].usr_sex = "F"
                }

            }

        }

        list[index].usr_country = binding.country.selectedCountryNameCode

        binding.country.setOnCountryChangeListener {
            list[index].usr_country = binding.country.selectedCountryNameCode
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(position: Int) {
//         for(i in 0 until list.size){
        list[index].checked = false
//         }
        index = position
        list[position].checked = true

        adapter.notifyDataSetChanged()

        binding.fname.setText(list[position].usr_firstname)
        binding.sname.setText(list[position].usr_surname)
        binding.dob.setText(list[position].usr_birth)
        binding.email.setText(list[position].usr_email)
        binding.pnumber.setText(list[position].usr_passport)
        binding.phone.setText(list[position].usr_phone)
        binding.address.setText(list[position].usr_home)
        binding.zip.setText(list[position].usr_zip)
        binding.country.setCountryForNameCode(list[position].usr_country);
        var p :Int;
        if(list[position].usr_sex == "M"){
            p = 0
        }else{
            p = 1
        }

        binding.gender.setSelection(p)
    }

    inner class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileVH>() {
        lateinit var context: Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileVH {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ViewProfileBinding = ViewProfileBinding.inflate(inflater, parent, false)
            return ProfileVH(binding);
        }

        override fun onBindViewHolder(holder: ProfileVH, position: Int) {
            val model = this@ProfileFragment.list[position];
            holder.itemView.setOnClickListener {
                this@ProfileFragment.setData(position)
            }
            if (model.checked) {
                holder.binding.verified.visibility = View.VISIBLE
            } else {
                holder.binding.verified.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return this@ProfileFragment.list.size
        }

        inner class ProfileVH(val binding: ViewProfileBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }
    }

    private fun startScanning() {
        // Settings for BlinkIdActivity
//        val settings = BlinkIdUISettings(mRecognizerBundle)
//
//        // tweak settings as you wish
//
//        // Start activity
//        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, settings)
    }

    fun familyregister(){
        sweet.show("Registering User")
        val url = Functions.concat(Constants.url , "FamilyRegistration.php");
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
                        if(context != null)
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }else{
                        if(context != null)
                        Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT)
                            .show()

                        list.removeAt(index);
                        if(list.isEmpty()){
                            val m = TProfileModel()
                            m.checked = true
                            list.add(m)
                        }
                        setData(0)
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                    sweet.dismiss()
                    if(context != null)
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            Response.ErrorListener{
                sweet.dismiss()
                if(context != null)
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val map : MutableMap<String, String> = HashMap()
                map["usr_email"] = binding.email.text.toString()
                map["token"] = Shared(requireContext()).getString("token")
                map["usr_firstname"] = binding.fname.text.toString()
                map["usr_surname"] = binding.sname.text.toString()
                map["usr_birth"] = binding.dob.text.toString()
                map["usr_home"] = binding.address.text.toString()
                map["usr_zip"] = binding.zip.text.toString()
                map["usr_passport"] = binding.pnumber.text.toString()
                map["usr_country"] = binding.country.selectedCountryName
                map["usr_phone"] = binding.phone.text.toString()
                if(binding.gender.selectedItemPosition == 0){
                    map["usr_sex"] = "M"
                }else{
                    map["usr_sex"] = "F"
                }
                return map
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


    fun getProfiles(){
        sweet.show("getting profiles")

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
                        if(list.size > 0) {
                            index = 0
                            setData(index)
                            list[0].checked = true;
                        }
                        adapter.notifyDataSetChanged()
                        clicks()
                        fielddata()
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