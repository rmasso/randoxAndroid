package com.demit.certify.Fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Cache
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Functions
import com.demit.certify.Extras.Shared
import com.demit.certify.Extras.Sweet
import com.demit.certify.Models.TProfileModel
import com.demit.certify.R
import com.demit.certify.data.ApiHelper
import com.demit.certify.databinding.FragmentProfileBinding
import com.demit.certify.databinding.ViewProfileBinding
import com.demit.certify.extensions.toBase64String
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.BlinkIdUISettings
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.view_profile.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var adapter: ProfileAdapter
    var currentDatePicked: Long = MaterialDatePicker.todayInUtcMilliseconds()
    var index = 0
    var list: MutableList<TProfileModel> = ArrayList();

    lateinit var sweet: Sweet

    //Blink Id Variables
    private lateinit var mRecognizer: BlinkIdCombinedRecognizer
    private lateinit var mRecognizerBundle: RecognizerBundle
    private val MY_REQUEST_CODE = 801
    private var isFromAdapterClick: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        sweet = Sweet(requireContext())



        getProfiles();
        adapter = ProfileAdapter()
        binding.rv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecognizer = BlinkIdCombinedRecognizer()
        mRecognizer.setReturnFaceImage(true)
        mRecognizer.setReturnFullDocumentImage(true)
        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = RecognizerBundle(mRecognizer)
        binding.gender.setSelection(0)
        binding.ethnicity.setSelection(0)
        shouldEnableFormFields(false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                // load the data into all recognizers bundled within your RecognizerBundle
                mRecognizerBundle.loadFromIntent(data)
                // now every recognizer object that was bundled within RecognizerBundle
                // has been updated with results obtained during scanning session
                // you can get the result by invoking getResult on recognizer
                binding.passportScanInfo.visibility = View.GONE
                val result = mRecognizer.result
                // result is valid, you can use it however you wish

                if (result.resultState == Recognizer.Result.State.Valid) {
                    val docImage = result.fullDocumentFrontImage?.convertToBitmap()
                    docImage?.let {
                        list[list.size - 1].usr_image = it.toBase64String()
                    }
                    setData(list.size - 1)



                    binding.rv.adapter?.itemCount?.minus(1)?.let { it1 ->
                        binding.rv.smoothScrollToPosition(
                            it1
                        )
                    }


                }
            }
        }

    }

    fun clicks() {
        binding.add.setOnClickListener() {
            binding.passportScanInfo.visibility = View.VISIBLE
            val p = TProfileModel()
            list.add(p)
            shouldEnableFormFields(true)
            binding.ethnicity.setSelection(0)
            binding.gender.setSelection(0)

        }

        binding.dob.setOnClickListener {
            openDatePicker()
        }

        binding.save.setOnClickListener {
            Log.d("++index", list[index].usr_image)
            if (list[index].usr_image == "") {
                if (context != null)
                    Toast.makeText(context, "Please Scan Passport", Toast.LENGTH_SHORT).show()
            } else if (binding.fname.text.toString().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Firstname can't be empty", Toast.LENGTH_SHORT).show()
            } else if (binding.dob.text.isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Date of birth can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.pnumber.text.isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Passport Number can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.address.text.toString() == "") {
                if (context != null)
                    Toast.makeText(context, "Address Can't be Empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.city.text.isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "City can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.zip.text.isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Zip Code can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.phone.text.toString().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Phone Number can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
                if (context != null)
                    Toast.makeText(context, "Invalid Email Address", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.confirmEmail.text.toString())
                    .matches()
            ) {
                if (context != null)
                    Toast.makeText(context, "Invalid Confirm Email Address", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.email.text.toString() != binding.confirmEmail.text.toString()) {
                if (context != null)
                    Toast.makeText(
                        context,
                        "Email and Confirm Email should match",
                        Toast.LENGTH_SHORT
                    ).show()

            } else if (binding.ethnicity.selectedItem == 0) {
                if (context != null)
                    Toast.makeText(context, "Choose Ethnicity", Toast.LENGTH_SHORT)
                        .show()
            } else {
                familyregister()
            }
        }

        binding.subInfo.verify.setOnClickListener {
            startScanning()

        }
        binding.subInfo.cancel.setOnClickListener {
            binding.passportScanInfo.visibility = View.GONE
        }
    }

    fun fielddata() {
        binding.fname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
              //  if (!isFromAdapterClick)
                    list[index].usr_firstname = s.toString()
                //else
                  //  isFromAdapterClick = false
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
                list[index].email = s.toString();
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

        binding.address2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_addressLine2 = s.toString();
            }

        })

        binding.city.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].usr_city = s.toString();
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

        val p: String = if (binding.gender.selectedItemPosition == 0) {
            "M"
        } else if (binding.gender.selectedItemPosition == 1) {
            "F"
        } else {
            "O"
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
                if (position == 0) {
                    list[index].usr_sex = "M"
                } else if (position == 1) {
                    list[index].usr_sex = "F"
                } else {
                    list[index].usr_sex = "O"
                }


            }

        }
        binding.ethnicity.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                list[index].ethnicity = resources.getStringArray(R.array.ethnicity)[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }





        list[index].usr_country = binding.country.selectedCountryNameCode

        binding.country.setOnCountryChangeListener {
            list[index].usr_country = binding.country.selectedCountryNameCode
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(position: Int) {

        list[index].checked = false
        adapter.notifyItemChanged(index)
        index = position
        list[position].checked = true
        adapter.notifyItemChanged(position)

        val fullName = "${list[position].usr_firstname} ${list[position].usr_surname}"
        binding.fname.setText(fullName)
        binding.dob.text = list[position].usr_birth
        binding.email.setText(list[position].email)
        binding.confirmEmail.setText(list[position].email)
        binding.pnumber.setText(list[position].usr_passport)
        binding.phone.setText(list[position].usr_phone)
        binding.address.setText(list[position].usr_home)
        binding.zip.setText(list[position].usr_zip)
        binding.country.setCountryForNameCode(list[position].usr_country)
        binding.city.setText(list[position].usr_city)
        binding.address2.setText(list[position].usr_addressLine2)
        binding.ethnicity.setSelection(
            resources.getStringArray(R.array.ethnicity).indexOf(list[position].ethnicity)
        )
        var p =
            when (list[position].usr_sex) {
                "M" -> {
                    0
                }
                "F" -> 1
                else -> 2
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
            val model = list[position]
            holder.itemView.setOnClickListener {
//                isFromAdapterClick = true
            val enable= model.usr_id==""
                shouldEnableFormFields(enable)

                this@ProfileFragment.setData(position)
            }
            if (model.checked) {
                holder.binding.verified.visibility = View.VISIBLE
            } else {
                holder.binding.verified.visibility = View.GONE
            }

            val is_url = Patterns.WEB_URL.matcher(model.usr_image).matches()
            val image = if (is_url) model.usr_image
            else Base64.decode(model.usr_image, Base64.DEFAULT)
            Log.d("++url++", model.usr_image)

            Glide.with(this@ProfileFragment)
                .load(image)
                .placeholder(R.drawable.profile)
                .into(holder.itemView.image)
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
        val settings = BlinkIdUISettings(mRecognizerBundle)

        // tweak settings as you wish

        // Start activity
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, settings)
    }

    fun familyregister() {
        if(list[index].usr_id!=""){
            Toast.makeText(requireContext(),"Edit Family user under development",Toast.LENGTH_SHORT).show()
            return
        }

        sweet.show("Registering User")


        val map: MutableMap<String, String> = HashMap()
        map["usr_email"] = list[index].email
        map["token"] = Shared(requireContext()).getString("token")
        map["usr_firstname"] = list[index].usr_firstname
        map["usr_surname"] = list[index].usr_surname
        map["usr_birth"] = list[index].usr_birth
        map["usr_home"] = list[index].usr_home
        map["usr_addressLine2"] = list[index].usr_addressLine2
        map["usr_city"] = list[index].usr_city
        map["usr_zip"] = list[index].usr_zip
        map["usr_passport"] = list[index].usr_passport
        map["usr_country"] = list[index].usr_country
        map["usr_phone"] = list[index].usr_phone
        map["usr_passport_image"] = list[index].usr_image
        map["usr_ethnicity"] = list[index].ethnicity
        map["usr_sex"] = when (binding.gender.selectedItemPosition) {
            0 -> {
                "M"
            }
            1 -> {
                "F"
            }
            else -> {
                "O"
            }
        }
        val gson = Gson()
        val json = gson.toJson(map)



        Log.d("++map", json)
        ApiHelper.postFamilyUser(map).observe(viewLifecycleOwner) { response ->
            sweet.dismiss()
            if (response == "0") {
                Toast.makeText(
                    context,
                    "User registered successfully",
                    Toast.LENGTH_SHORT
                ).show()
                list[index].usr_id="1"
                shouldEnableFormFields(false)
            } else {
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        /* val url = Functions.concat(Constants.url, "FamilyRegistration.php");
         val request: StringRequest = object : StringRequest(
             Method.POST,
             url,
             Response.Listener {
                 sweet.dismiss()
                 Log.d("sss", it.toString())
                 try {
                     val obj = JSONObject(it)
                     val s = obj.getString("ret");
                     if (s == "100") {
                         if (context != null)
                             Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                 .show()
                     } else {
                         if (context != null)
                             Toast.makeText(
                                 context,
                                 "User registered successfully",
                                 Toast.LENGTH_SHORT
                             ).show()
                     }
                 } catch (e: Exception) {
                     e.printStackTrace()
                     sweet.dismiss()
                     if (context != null)
                         Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                             .show()
                 }
             },
             Response.ErrorListener {
                 sweet.dismiss()
                 if (context != null)
                     Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                         .show()
             }
         ) {
             override fun getParams(): MutableMap<String, String> {
                 val map: MutableMap<String, String> = HashMap()
                 map["usr_email"] =list[index].email
                 map["token"] = Shared(requireContext()).getString("token")
                 map["usr_firstname"] = list[index].usr_firstname
                 map["usr_surname"] = list[index].usr_surname
                 map["usr_birth"] = list[index].usr_birth
                 map["usr_home"] =list[index].usr_home
                 map["usr_addressLine2"] = list[index].usr_addressLine2
                 map["usr_city"] = list[index].usr_city
                 map["usr_zip"] =list[index].usr_zip
                 map["usr_passport"] =list[index].usr_passport
                 map["usr_country"] = list[index].usr_country
                 map["usr_phone"] =list[index].usr_phone
                 map["usr_passport_image"] = list[index].usr_image
                 map["usr_ethnicity"] =
                     resources.getStringArray(R.array.ethnicity)[binding.ethnicity.selectedItemPosition]
                 map["usr_sex"] = when (binding.gender.selectedItemPosition) {
                     0 -> {
                         "M"
                     }
                     1 -> {
                         "F"
                     }
                     else -> {
                         "O"
                     }
                 }

                 for (key in map.keys)
                     Log.d("++map++", "${list[index]} ${map.keys.size}")
                 return map
             }
         }

         request.retryPolicy = DefaultRetryPolicy(
             50000,
             DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
             DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
         )

         request.setShouldCache(false)
         if (context != null) {
             val queue = Volley.newRequestQueue(context)
             queue.cache.clear()
             queue.add(request)
         }*/

    }


    fun getProfiles() {
        sweet.show("getting profiles")

        val url = Functions.concat(Constants.url, "getProfile.php");
        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                sweet.dismiss()
                Log.d("sss", it.toString())

                try {
                    val obj = JSONObject(it)
                    val s = obj.getString("ret");
                    if (s == "100") {
                        if (context != null) {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        val gson = Gson()
                        val listType = object : TypeToken<List<TProfileModel?>?>() {}.type
                        val sliderItem: MutableList<TProfileModel> =
                            gson.fromJson(obj.get("ret").toString(), listType)
                        list = sliderItem
                        if (list.size > 0) {
                            index = 0
                            setData(index)
                            list[0].checked = true;
                        }
                        adapter.notifyDataSetChanged()
                        clicks()
                        fielddata()
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
                if (context != null) {
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
        request.setShouldCache(false)
        if (context != null) {
            val queue = Volley.newRequestQueue(context)
            queue.cache.clear()
            queue.add(request)
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = currentDatePicked
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setEnd(calendar.timeInMillis)
                .setValidator(DateValidatorPointBackward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Birth Date")
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(currentDatePicked)
            .build()
        datePicker.addOnPositiveButtonClickListener {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            val textDate = dateFormat.format(Date(it))
            binding.dob.text = textDate
            list[index].usr_birth = textDate
            currentDatePicked = it

            datePicker.dismiss()
        }
        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.show(requireActivity().supportFragmentManager, "date_picker")

    }

    private fun shouldEnableFormFields(enable:Boolean){
        with(binding){
            fname.isEnabled= enable
            dob.isEnabled= enable
           val color= if(enable)
                ContextCompat.getColor(requireContext(),R.color.black)
            else
                ContextCompat.getColor(requireContext(),R.color.textHint)
                dob.setTextColor(color)

            pnumber.isEnabled= enable
            phone.isEnabled= enable
            country.isClickable=enable
            gender.isEnabled= enable
            ethnicity.isEnabled= enable
            address.isEnabled= enable
            city.isEnabled= enable
            zip.isEnabled= enable
            email.isEnabled= enable
            confirmEmail.isEnabled= enable
            address2.isEnabled= enable

        }
    }
}