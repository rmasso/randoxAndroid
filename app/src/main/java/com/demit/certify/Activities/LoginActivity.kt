package com.demit.certify.Activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Functions
import com.demit.certify.Extras.Shared
import com.demit.certify.Extras.Sweet
import com.demit.certify.databinding.ActivityLoginBinding
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    lateinit var binding :ActivityLoginBinding
    lateinit var sweet : Sweet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sweet = Sweet(this)
        clicks()
    }

    private fun clicks() {
        binding.signin.setOnClickListener{
            if(!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()){
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            }else if(binding.email.text.toString().isEmpty()){
                Toast.makeText(this, "Password cant be empty", Toast.LENGTH_SHORT).show()
            }else{
                requestUploadSurvey(this)
            }

        }
        binding.scan.setOnClickListener {
//            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
//                startActivity(Intent(this,ScanActivity::class.java))
//                return@setOnClickListener
//            }
//            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//                startActivity(Intent(this,ScanActivity::class.java))
//            }else{
//                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),1)
//            }

            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

    private fun requestUploadSurvey(context : Context) {
        sweet.show("Loging in");
        val webServicesAPI = ApiService2.retrofit
            .create(WebServicesAPI::class.java)
        var surveyResponse: Call<Any?>? = null
        var email: RequestBody = RequestBody.create(MediaType.parse("text/plain"), binding.email.text.toString())
        var password: RequestBody = RequestBody.create(MediaType.parse("text/plain"), binding.password.text.toString())




        surveyResponse = webServicesAPI.uploadSurvey(
            email, password
        )
        surveyResponse!!.enqueue(object : Callback<Any?> {

            override fun onResponse(call: Call<Any?>, response: retrofit2.Response<Any?>) {
                sweet.dismiss()
                try {
                    Log.d("res" , response.body().toString())
                    val obj = JSONObject(response.body().toString())
                    val s = obj.getString("ret");
                    if(s == "100"){
                        Toast.makeText(context,"Invalid credentials",Toast.LENGTH_SHORT).show()
                    }else{
                        Shared(context).setString("token" , s)
                        startActivity(Intent(context,DashboardActivity::class.java))
                    }
                } catch (e: java.lang.Exception) {
                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                sweet.dismiss()
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface WebServicesAPI {
        @Multipart
        @POST("getLogin.php")
        fun uploadSurvey(
            @Part("usr_email") email: RequestBody,
            @Part("usr_pwd") password: RequestBody
        ): Call<Any?>?
    }
    object ApiService2 {
        private const val BASE_URL = Constants.url
        val retrofit: Retrofit
            get() {
                val okHttpClient = OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
                return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            }
    }
}
