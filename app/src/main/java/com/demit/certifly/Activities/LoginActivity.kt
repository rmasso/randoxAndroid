package com.demit.certifly.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demit.certifly.Extras.Constants
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.ActivityLoginBinding
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
    lateinit var binding: ActivityLoginBinding
    lateinit var sweet: Sweet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sweet = Sweet(this)
        clicks()
        val sharedPreferences = Shared(this)
        val email = sharedPreferences.getString("email")
        val password = sharedPreferences.getString("password")
        binding.email.setText(email)
        binding.password.setText(password)
    }

    private fun clicks() {
        binding.signin.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            } else if (binding.password.text.toString().isEmpty()) {
                Toast.makeText(this, "Password can't be empty", Toast.LENGTH_SHORT).show()
            } else {
                requestUploadSurvey(this)
            }

        }
        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnForgot.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
                Toast.makeText(this, "Invalid Email.", Toast.LENGTH_SHORT).show()
            } else {
                sweet.show("Please Wait")
                ApiHelper.forgetPassword(
                    Shared(this@LoginActivity).getString("token"),
                    binding.email.text.toString()
                )
                    .observe(this, { response ->
                        if (response == "0") {
                            sweet.dismiss()
                            Toast.makeText(
                                this@LoginActivity,
                                "An email has been sent to you. You can check your new password.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            sweet.dismiss()
                            if (response == "100")
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Email not registered.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            else
                                Toast.makeText(this@LoginActivity, response, Toast.LENGTH_SHORT)
                                    .show()
                        }
                    })
            }
        }
    }

    private fun requestUploadSurvey(context: Context) {
        sweet.show("Logging In");
        val webServicesAPI = ApiService2.retrofit
            .create(WebServicesAPI::class.java)
        var surveyResponse: Call<Any?>? = null
        var email: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), binding.email.text.toString())
        var password: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), binding.password.text.toString())
        var companyName = RequestBody.create(MediaType.parse("text/plain"), "Randox")



        surveyResponse = webServicesAPI.uploadSurvey(
            email, password, companyName
        )
        surveyResponse!!.enqueue(object : Callback<Any?> {

            override fun onResponse(call: Call<Any?>, response: retrofit2.Response<Any?>) {
                sweet.dismiss()
                try {
                    Log.d("res", response.body().toString())
                    val obj = JSONObject(response.body().toString())
                    val s = obj.getString("ret");
                    if (s == "100.0") {
                        Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    } else {
                        val sharedPreferences = Shared(context)
                        sharedPreferences.setString("token", s)
                        sharedPreferences.setString("email", binding.email.text.toString())
                        sharedPreferences.setString("password", binding.password.text.toString())
                        startActivity(Intent(context, DashboardActivity::class.java))
                        this@LoginActivity.finish()
                    }
                } catch (e: java.lang.Exception) {

                    Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                sweet.dismiss()

                Toast.makeText(context, call.request().body().toString(), Toast.LENGTH_SHORT).show()
//                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface WebServicesAPI {
        @Multipart
        @POST("getLogin.php")
        fun uploadSurvey(
            @Part("usr_email") email: RequestBody,
            @Part("usr_pwd") password: RequestBody,
            @Part("companyName") companyName: RequestBody
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
