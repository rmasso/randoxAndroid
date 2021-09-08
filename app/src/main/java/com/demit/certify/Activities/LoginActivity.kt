package com.demit.certify.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Functions
import com.demit.certify.Extras.Shared
import com.demit.certify.Extras.Sweet
import com.demit.certify.databinding.ActivityLoginBinding
import org.json.JSONObject

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
                login()
            }

        }
        binding.scan.setOnClickListener {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
//                startActivity(Intent(this,ScanActivity::class.java))
                return@setOnClickListener
            }
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//                startActivity(Intent(this,ScanActivity::class.java))
            }else{
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),1)
            }

        }
    }

    fun login(){
        sweet.show("Loging in")
        val obj = JSONObject();
        obj.put("usr_email", binding.email.text.toString())
        obj.put("usr_pwd", binding.email.text.toString())

        val url = Functions.concat(Constants.url , "getLogin.php");
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            obj,
            Response.Listener{
                sweet.dismiss()
                try{
                    val s = it.getString("ret");
                    if(s == "100"){
                        Toast.makeText(this,"Invalid credentials",Toast.LENGTH_SHORT).show()
                    }else{
                        Shared(this).setString("token" , s)
                        startActivity(Intent(this,DashboardActivity::class.java))
                    }
                }catch (e : Exception){
                    sweet.dismiss()
                    Toast.makeText(this,"Invalid credentials",Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener{
                sweet.dismiss()
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()

            }
        ){}

        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,)
        Volley.newRequestQueue(this).add(request)
    }
}