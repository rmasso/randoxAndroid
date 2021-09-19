package com.demit.certify.Activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Functions
import com.demit.certify.Extras.Sweet
import com.demit.certify.databinding.ActivityProfileBinding
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.BlinkIdUISettings
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject
import java.util.*

class RegisterActivity : AppCompatActivity() {
    //Blink Id Variables
    private lateinit var mRecognizer: BlinkIdCombinedRecognizer
   private lateinit var mRecognizerBundle: RecognizerBundle
    private val MY_REQUEST_CODE = 801
    lateinit var sweet : Sweet
    lateinit var binding : ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sweet = Sweet(this)
        clicks()
        mRecognizer = BlinkIdCombinedRecognizer()
        mRecognizer.setReturnFaceImage(true)
        mRecognizerBundle = RecognizerBundle(mRecognizer)
    }
    private fun clicks() {
        Log.d("ssss" , "here")
        binding.save.setOnClickListener {
            Log.d("ssss" , "here")
            if(binding.fname.text.toString().isEmpty()){
                Toast.makeText(this, "Firstname can't be empty",Toast.LENGTH_SHORT).show()
            }else if(binding.sname.text.toString().isEmpty()){
                Toast.makeText(this, "Surname can't be empty",Toast.LENGTH_SHORT).show()
            }else
            if(!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()){
                Toast.makeText(this, "Invalid Email Address",Toast.LENGTH_SHORT).show()
            }else if(binding.password.text.toString().isEmpty()){
                Toast.makeText(this, "Password can't be empty",Toast.LENGTH_SHORT).show()
            }else if(binding.dob.text.toString().isEmpty()){
                Toast.makeText(this, "Date of birth can't be empty",Toast.LENGTH_SHORT).show()
            }else{
                register()
            }
        }

        binding.scanYourPassport.setOnClickListener {
                startScanning()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                // load the data into all recognizers bundled within your RecognizerBundle
                mRecognizerBundle.loadFromIntent(data)

                // now every recognizer object that was bundled within RecognizerBundle
                // has been updated with results obtained during scanning session
                // you can get the result by invoking getResult on recognizer
                val result = mRecognizer.result
                // result is valid, you can use it however you wish
                if (result.resultState == Recognizer.Result.State.Valid) {
                    fname.text= Editable.Factory.getInstance().newEditable( result.firstName)
                    sname.text= Editable.Factory.getInstance().newEditable( result.lastName)


                    result.dateOfBirth.date?.let {date->
                        val day= date.day
                        val year= date.year
                        val month= date.month
                        dob.text= Editable.Factory.getInstance().newEditable("$day/$month/$year")
                    }


                    pnumber.text= Editable.Factory.getInstance().newEditable( result.documentNumber)




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
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
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
                val map : MutableMap<String, String> = HashMap()
                map["usr_email"] = binding.email.text.toString()
                map["usr_pwd"] = binding.password.text.toString()
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

        val queue = Volley.newRequestQueue(this);
        queue.cache.clear();
        queue.add(request)

    }
}