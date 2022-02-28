package com.demit.certifly.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demit.certifly.BuildConfig
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Fragments.PermissionDialog
import com.demit.certifly.R
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var sweet: Sweet
    var isPasswordShown = false

    @SuppressLint("SetTextI18n")
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

        binding.passwordVisibilityToggle.setOnClickListener {
            //We have to show password to user in form of plain text
            binding.password.transformationMethod = if (!isPasswordShown) {
                binding.passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off_eye)
                isPasswordShown = true
                HideReturnsTransformationMethod.getInstance()

            } else {
                binding.passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_on_eye)
                isPasswordShown = false
                PasswordTransformationMethod.getInstance()
            }
            binding.password.setSelection(binding.password.length())
        }

        binding.appVersion.text = "v${BuildConfig.VERSION_NAME}"
        binding.privacy.text= getText(R.string.privacy_policy)
        binding.privacy.setOnClickListener {
            openBrowser()
        }

        if (Shared(this@LoginActivity).getString("has_agreed") != "yes"){
            val permissionDialog= PermissionDialog{id->
                if(id==R.id.btn_agree){
                    Shared(this@LoginActivity).setString("has_agreed","yes")
                }
            }.apply {
                isCancelable=false
            }
            permissionDialog.show(supportFragmentManager,"consent_dialog")
        }

    }

    private fun openBrowser() {
        val url = "https://www.randoxhealth.com/randox-certifly-privacy-policy"
        val privacyIntent = Intent(Intent.ACTION_VIEW)
        privacyIntent.data = Uri.parse(url)
        startActivity(privacyIntent)
    }


    private fun clicks() {
        binding.signin.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            } else if (binding.password.text.toString().isEmpty()) {
                Toast.makeText(this, "Password can't be empty", Toast.LENGTH_SHORT).show()
            } else {
                login()
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
                    binding.email.text.toString()
                )
                    .observe(this, { response ->
                        sweet.dismiss()
                        if (response.success) {
                            Toast.makeText(
                                this@LoginActivity,
                                "An email has been sent to you. You can check your new password.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }
        }
    }

    private fun login() {
        sweet.show("Logging In")
        ApiHelper.login(binding.email.text.toString(), binding.password.text.toString())
            .observe(this, { response ->

                if (response.errorMessage.isNotEmpty()) {
                    sweet.dismiss()
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG).show()
                } else {
                    //Login
                    sweet.dismiss()
                    val sharedPreferences = Shared(this)
                    sharedPreferences.setString("token", response.accessToken)
                    sharedPreferences.setString("email", binding.email.text.toString())
                    sharedPreferences.setString("password", binding.password.text.toString())
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()

                }

            })
    }
}
