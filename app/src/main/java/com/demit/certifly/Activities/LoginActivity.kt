package com.demit.certifly.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.demit.certifly.BuildConfig
import com.demit.certifly.Extras.Constants
import com.demit.certifly.Extras.DetachableClickListener
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Fragments.PermissionDialog
import com.demit.certifly.R
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
import java.util.jar.Manifest

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var sweet: Sweet
    val permissions = arrayListOf<String>()
    var isPasswordShown=false
    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermissions()
        sweet = Sweet(this)
        clicks()
        val sharedPreferences = Shared(this)
        val email = sharedPreferences.getString("email")
        val password = sharedPreferences.getString("password")
        binding.email.setText(email)
        binding.password.setText(password)

        permissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResultsMap ->
                if (grantResultsMap.containsValue(false))
                    showAlertDialog()
            }

        binding.passwordVisibilityToggle.setOnClickListener {
            //We have to show password to user in form of plain text
            binding.password.transformationMethod =  if(!isPasswordShown){
                binding.passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off_eye)
                isPasswordShown=true
                HideReturnsTransformationMethod.getInstance()

            }else{
                binding.passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_on_eye)
                isPasswordShown=false
                PasswordTransformationMethod.getInstance()
            }
            binding.password.setSelection(binding.password.length())
        }

        binding.appVersion.text= "v${BuildConfig.VERSION_NAME}"

    }

    private fun checkPermissions() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(android.Manifest.permission.CAMERA)
        }
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        val writePermissionGranted = hasWritePermission || minSdk29

        if (!writePermissionGranted) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissions.isNotEmpty()) {

            var permissionDialog = PermissionDialog { btnClickId ->
                when (btnClickId) {
                    R.id.agreeBtn -> {
                        permissionResultLauncher.launch(permissions.toArray(arrayOf<String>()))
                    }
                    R.id.cancelBtn -> {
                        showAlertDialog()
                    }
                }
            }
            permissionDialog.isCancelable = false
            permissionDialog.show(supportFragmentManager, "permission_dialog")
        }
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

    private fun showAlertDialog() {

        val positiveClickListener = DetachableClickListener.wrap { _, _ ->
            permissionResultLauncher.launch(
                permissions.toArray(arrayOf<String>())
            )
        }

        val negativeClickListener = DetachableClickListener.wrap { _, _ -> finish() }


        val builder = AlertDialog.Builder(this@LoginActivity)
            .setTitle(R.string.missing_permissions)
            .setMessage(R.string.you_have_to_grant_permissions)
            .setPositiveButton(R.string.ok, positiveClickListener)
            .setNegativeButton(R.string.no_close_the_app, negativeClickListener)
            .create()

        //avoid memory leaks
        positiveClickListener.clearOnDetach(builder)
        negativeClickListener.clearOnDetach(builder)
        builder.show()
    }
}
