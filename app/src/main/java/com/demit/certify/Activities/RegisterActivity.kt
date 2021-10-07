package com.demit.certify.Activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Functions
import com.demit.certify.Extras.Shared
import com.demit.certify.Extras.Sweet
import com.demit.certify.R
import com.demit.certify.databinding.ActivityProfileBinding
import com.demit.certify.extensions.toBase64String
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
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

class RegisterActivity : AppCompatActivity() {
    //Blink Id Variables
    private lateinit var mRecognizer: BlinkIdCombinedRecognizer
   private lateinit var mRecognizerBundle: RecognizerBundle
   private var usr_img=""
    private val MY_REQUEST_CODE = 801
    lateinit var sweet : Sweet
    lateinit var binding : ActivityProfileBinding
    var currentDatePicked: Long = MaterialDatePicker.todayInUtcMilliseconds()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sweet = Sweet(this)
        clicks()
        mRecognizer = BlinkIdCombinedRecognizer()
        mRecognizer.setReturnFaceImage(true)
        mRecognizer.setReturnFullDocumentImage(true)
        mRecognizerBundle = RecognizerBundle(mRecognizer)
        binding.ethnicity.setSelection(0)
        binding.passportScanInfo.visibility= View.VISIBLE
    }
    private fun clicks() {
        Log.d("ssss" , "here")
        binding.save.setOnClickListener {
            if(usr_img==""){
                Toast.makeText(this, "Passport Image Missing", Toast.LENGTH_SHORT).show()
            }
           else if (binding.fname.text.toString().isEmpty()) {
                    Toast.makeText(this, "Firstname Missing", Toast.LENGTH_SHORT).show()
            }else if (binding.sname.text.isEmpty()) {
                Toast.makeText(this, "Surname Missing", Toast.LENGTH_SHORT)
                    .show()
            }

            else if (binding.dob.text.isEmpty()) {

                    Toast.makeText(this, "Date of birth Missing", Toast.LENGTH_SHORT)
                        .show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
                Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.confirmEmail.text.toString())
                    .matches()
            ) {
                Toast.makeText(this, "Invalid Confirm Email Address", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.email.text.toString() != binding.confirmEmail.text.toString()) {
                Toast.makeText(
                    this,
                    "Email and Confirm Email should match",
                    Toast.LENGTH_SHORT
                ).show()

            }else if(binding.password.text.toString().isEmpty()){
                Toast.makeText(this, "Password Missing",Toast.LENGTH_SHORT).show()
            }

            else if (binding.pnumber.text.isEmpty()) {
                    Toast.makeText(this, "Passport Number Missing", Toast.LENGTH_SHORT)
                        .show()
            }else if (binding.phone.text.toString().isEmpty()) {
                Toast.makeText(this, "Phone Number Missing", Toast.LENGTH_SHORT)
                    .show()
            }

            else if (binding.address.text.toString() == "") {
                    Toast.makeText(this, "Address Missing", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.city.text.isEmpty()) {
                    Toast.makeText(this, "City Missing", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.zip.text.isEmpty()) {
                    Toast.makeText(this, "Zip Code Missing", Toast.LENGTH_SHORT)
                        .show()
            }  else if (binding.ethnicity.selectedItem == 0) {
                    Toast.makeText(this, "Ethnicity Missing", Toast.LENGTH_SHORT)
                        .show()
            }else{
                register()
            }
        }

        binding.scanYourPassport.setOnClickListener {
            binding.passportScanInfo.visibility= View.VISIBLE

        }

        binding.subInfo.verify.setOnClickListener {
            startScanning()

        }
        binding.subInfo.cancel.setOnClickListener {
            binding.passportScanInfo.visibility= View.GONE
        }

        binding.dob.setOnClickListener {
            openDatePicker()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                // load the data into all recognizers bundled within your RecognizerBundle
                binding.passportScanInfo.visibility= View.GONE
                mRecognizerBundle.loadFromIntent(data)

                // now every recognizer object that was bundled within RecognizerBundle
                // has been updated with results obtained during scanning session
                // you can get the result by invoking getResult on recognizer
                val result = mRecognizer.result
                // result is valid, you can use it however you wish
                if (result.resultState == Recognizer.Result.State.Valid) {
                    val docImage = result.fullDocumentFrontImage?.convertToBitmap()
                    docImage?.let {
                        usr_img =  it.toBase64String()
                        image.visibility= View.VISIBLE
                        Glide.with(this)
                            .load(Base64.decode(usr_img, Base64.DEFAULT))
                            .placeholder(R.drawable.profile)
                            .into(binding.image)

                    }

                }
            }
        }
    }

    private fun startScanning() {
        // Settings for BlinkIdActivity
        val settings = BlinkIdUISettings(mRecognizerBundle)

        // tweak settings as you wish

        // Start activity
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, settings)
    }

    fun register(){
        sweet.show("Registering User")
        val url = Functions.concat(Constants.url , "UserRegistration.php");
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
                        Toast.makeText(this, "User already registered", Toast.LENGTH_SHORT)
                            .show()

                    }else{
                        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                    sweet.dismiss()

                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            Response.ErrorListener{
                sweet.dismiss()


//                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()

            }
        ){
            override fun getParams(): MutableMap<String, String> {

                /*
                * "usr_email=" + usr_email
+ "&usr_pwd=" + usr_pwd
+ "&usr_firstname=" + usr_firstname
+ "&usr_surname=" + usr_surname
+ "&usr_sex=" + usr_sex
+ "&usr_birth=" + usr_birth
+ "&usr_passport=" + usr_passport
+ "&usr_country=" + usr_country
+ "&usr_home=" + usr_home
+ "&usr_addressLine2=" + usr_addressLine2
+ "&usr_city=" + usr_city
+ "&usr_zip=" + usr_zip
+ "&usr_phone=" + usr_phone
+ "&usr_passport_image=" + strBase64
+ "&usr_ethnicity=" + usr_ethnicity*/
                val map : MutableMap<String, String> = HashMap()
                map["usr_email"] = binding.email.text.toString()
                map["usr_pwd"] = binding.password.text.toString()
                map["usr_firstname"] = binding.fname.text.toString()
                map["usr_surname"] = binding.sname.text.toString()
                map["usr_birth"] = binding.dob.text.toString()
                map["usr_home"] = binding.address.text.toString()
                map["usr_addressLine2"] = binding.address2.text.toString()
                map["usr_city"] = binding.city.text.toString()
                map["usr_zip"] = binding.zip.text.toString()
                map["usr_passport"] = binding.pnumber.text.toString()
                map["usr_country"] = binding.country.selectedCountryName
                map["usr_phone"] = binding.phone.text.toString()
                map["usr_passport_image"] = usr_img
                map["usr_ethnicity"] =
                    resources.getStringArray(R.array.ethnicity)[binding.ethnicity.selectedItemPosition]
                if (binding.gender.selectedItemPosition == 0) {
                    map["usr_sex"] = "M"
                } else if(binding.gender.selectedItemPosition == 1) {
                    map["usr_sex"] = "F"
                }else{
                    map["usr_sex"] = "O"
                }


                return map
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
        )

        val queue = Volley.newRequestQueue(this);
        queue.cache.clear();
        queue.add(request)

    }

    private fun openDatePicker() {

        // Build constraints.

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis= currentDatePicked
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
            currentDatePicked = it

            datePicker.dismiss()
        }
        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.show(supportFragmentManager, "date_picker")

    }
}