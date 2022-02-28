package com.demit.certifly.Activities


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.demit.certifly.Extras.Constants.Companion.DIALOG_TYPE_CAMERA_PASSPORT
import com.demit.certifly.Extras.PermissionUtil.CAMERA_PERMISSION
import com.demit.certifly.Extras.PermissionUtil.hasCameraPermission
import com.demit.certifly.Extras.PermissionUtil.isMarshMallowOrAbove
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Fragments.PermissionInfoDialog
import com.demit.certifly.R
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.ActivityProfileBinding
import com.demit.certifly.extensions.toBase64String
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.BlinkIdUISettings
import kotlinx.android.synthetic.main.activity_profile.*
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    //Blink Id Variables
    private lateinit var mRecognizer: BlinkIdCombinedRecognizer
    private lateinit var mRecognizerBundle: RecognizerBundle
    private val MY_REQUEST_CODE = 801

    //Camera Permission Variables
    lateinit var cameraRequestLauncher: ActivityResultLauncher<String>
    var enable = false
    var isPasswordShown = false
    private val CAMERA_REQUEST_CODE = 1001

    private var usr_img = ""
    lateinit var sweet: Sweet
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enable =
            getSharedPreferences("app", MODE_PRIVATE).getBoolean("should_take_to_settings", false)
        sweet = Sweet(this)
        clicks()
        mRecognizer = BlinkIdCombinedRecognizer()
        mRecognizer.setReturnFaceImage(true)
        mRecognizer.setReturnFullDocumentImage(true)
        mRecognizerBundle = RecognizerBundle(mRecognizer)
        binding.ethnicity.setSelection(0)
        binding.passportScanInfo.visibility = View.VISIBLE

        val text =
            "<a href='https://www.randoxhealth.com/covid-19-terms-and-conditions'> I agree to the accuracy of the submitted data\n" +
                    " and agree to the  Terms and Conditions. </a>"

        binding.link.isClickable = true
        binding.link.movementMethod = LinkMovementMethod.getInstance()
        val htmlOut = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        else
            Html.fromHtml(text)
        binding.link.text = htmlOut

        if (isMarshMallowOrAbove()) {
            cameraRequestLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        getSharedPreferences("app", MODE_PRIVATE).edit()
                            .putBoolean("should_take_to_settings", false).apply()
                        startScanning()
                    } else {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            if (!shouldShowRequestPermissionRationale(CAMERA_PERMISSION)) {
                                enable = true
                                getSharedPreferences("app", MODE_PRIVATE).edit()
                                    .putBoolean("should_take_to_settings", true).apply()
                            }

                        } else {
                            //Android 11 and up
                            enable = true
                            getSharedPreferences("app", MODE_PRIVATE).edit()
                                .putBoolean("should_take_to_settings", true).apply()
                        }

                    }

                }
        }
    }

    private fun clicks() {
        Log.d("ssss", "here")
        binding.save.setOnClickListener {
            if (usr_img == "") {
                Toast.makeText(this, "Passport Image Missing", Toast.LENGTH_SHORT).show()
            } else if (binding.fname.text.trim().isEmpty()) {
                Toast.makeText(this, "Firstname Missing", Toast.LENGTH_SHORT).show()
            } else if (binding.sname.text.trim().isEmpty()) {
                Toast.makeText(this, "Surname Missing", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.dob.text.trim().isEmpty()) {

                Toast.makeText(this, "Date of birth Missing", Toast.LENGTH_SHORT)
                    .show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.trim()).matches()) {
                Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show()
            }  else if (!binding.email.text.toString().trim().equals(binding.confirmEmail.text.toString().trim(),true)) {
                Toast.makeText(
                    this,
                    "Email and Confirm Email should match",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (binding.password.text.trim().isEmpty()) {
                Toast.makeText(this, "Password Missing", Toast.LENGTH_SHORT).show()
            } else if (binding.pnumber.text.trim().isEmpty()) {
                Toast.makeText(this, "Passport Number Missing", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.phone.text.trim().toString().isEmpty()) {
                Toast.makeText(this, "Phone Number Missing", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.address.text.trim().toString().isEmpty()) {
                Toast.makeText(this, "Address Missing", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.city.text.trim().isEmpty()) {
                Toast.makeText(this, "City Missing", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.zip.text.trim().isEmpty()) {
                Toast.makeText(this, "Zip Code Missing", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.ethnicity.selectedItemPosition == 0) {
                Toast.makeText(this, "Choose Ethnicity", Toast.LENGTH_SHORT)
                    .show()
            } else if (!binding.radio.isChecked) {
                Toast.makeText(
                    this,
                    "Please Confirm that you agree with our Terms&conditions",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                signup()
            }
        }

        binding.scanYourPassport.setOnClickListener {
            binding.passportScanInfo.visibility = View.VISIBLE

        }

        binding.subInfo.verify.setOnClickListener {
            // startScanning()
            if (isMarshMallowOrAbove()) {
                requestCameraPermission()
            } else {
                startScanning()
            }

        }
        binding.subInfo.cancel.setOnClickListener {
            onBackPressed()
        }

        binding.dob.setOnClickListener {
            openDatePicker()
        }

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

    }



    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                // load the data into all recognizers bundled within your RecognizerBundle
                binding.passportScanInfo.visibility = View.GONE
                mRecognizerBundle.loadFromIntent(data)

                // now every recognizer object that was bundled within RecognizerBundle
                // has been updated with results obtained during scanning session
                // you can get the result by invoking getResult on recognizer
                val result = mRecognizer.result
                // result is valid, you can use it however you wish
                if (result.resultState == Recognizer.Result.State.Valid) {
                    val docImage = result.fullDocumentFrontImage?.convertToBitmap()
                    val mrzResult = result.mrzResult
                    docImage?.let {
                        usr_img = it.toBase64String()
                        image.visibility = View.VISIBLE
                        Glide.with(this)
                            .load(Base64.decode(usr_img, Base64.DEFAULT))
                            .placeholder(R.drawable.profile)
                            .into(binding.image)
                    }

                    mrzResult?.let { mrzCodeResult ->

                        val firstName = mrzCodeResult.secondaryId
                        val lastName = mrzCodeResult.primaryId
                        val dateOfBirth = with(mrzCodeResult.dateOfBirth.date) {
                            String.format("%02d-%02d-%04d", this?.day, this?.month, this?.year)
                        }
                        val passport = mrzCodeResult.documentNumber
                        with(binding) {
                            fname.text = Editable.Factory.getInstance().newEditable(firstName)
                            sname.text = Editable.Factory.getInstance().newEditable(lastName)
                            dob.text = Editable.Factory.getInstance().newEditable(dateOfBirth)
                            pnumber.text = Editable.Factory.getInstance().newEditable(passport)
                        }
                    }

                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }
        }

        else if(requestCode==CAMERA_REQUEST_CODE){
            if(hasCameraPermission(this)){
                getSharedPreferences("app", MODE_PRIVATE).edit()
                    .putBoolean("should_take_to_settings", false).apply()
                startScanning()
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

    private fun requestCameraPermission() {
        when {
            hasCameraPermission(this) -> {
                startScanning()
            }
            enable -> {
                val permissionSettingsDialog =
                    PermissionInfoDialog(true, DIALOG_TYPE_CAMERA_PASSPORT) { btnClickId,dialog ->
                        when (btnClickId) {
                            R.id.btn_settings -> {
                                val intent =  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                val uri = Uri.fromParts("package", packageName, null);
                                intent.data = uri;
                                startActivityForResult(intent, 1001)
                                dialog.dismiss()
                            }
                            R.id.btn_not_now -> {
                                dialog.dismiss()
                            }
                        }
                    }
                permissionSettingsDialog.isCancelable=false
                permissionSettingsDialog.show(supportFragmentManager,"permission_dialog")


            }
            else -> {
                //Show some cool ui to the user explaining why we use this permission
                val permissionDialog =
                    PermissionInfoDialog(false, DIALOG_TYPE_CAMERA_PASSPORT) { btnClickId,dialog ->
                        when (btnClickId) {
                            R.id.btn_allow -> {
                                cameraRequestLauncher.launch(CAMERA_PERMISSION)
                                dialog.dismiss()
                            }
                            R.id.btn_not_now -> {
                                dialog.dismiss()
                            }
                        }
                    }
                permissionDialog.isCancelable=false
                permissionDialog.show(supportFragmentManager,"permission_dialog")

            }
        }
    }

    fun signup() {
        val map: MutableMap<String, String> = HashMap()
        map["email"] = binding.email.text.toString().trim()
        map["password"] = binding.password.text.toString().trim()
        map["usr_parent_id"] = "0"
        map["usr_name"] = binding.fname.text.toString().trim()
        map["usr_firstname"] = binding.fname.text.toString().trim()
        map["usr_surname"] = binding.sname.text.toString().trim()
        map["usr_birth"] = binding.dob.text.toString().trim()
        map["usr_home"] = binding.address.text.toString().trim()
        map["usr_addressLine2"] = binding.address2.text.toString().trim()
        map["usr_admin"] = "N"
        map["usr_city"] = binding.city.text.toString().trim()
        map["usr_zip"] = binding.zip.text.toString().trim()
        map["usr_passport"] = binding.pnumber.text.toString().trim()
        map["usr_country"] = binding.country.selectedCountryNameCode
        map["usr_phone"] = binding.phone.text.toString().trim()
        map["usr_passport_image"] = usr_img
        map["usr_ethnicity"] =
            resources.getStringArray(R.array.ethnicity)[binding.ethnicity.selectedItemPosition]
        when (binding.gender.selectedItemPosition) {
            0 -> {
                map["usr_sex"] = "Male"
            }
            1 -> {
                map["usr_sex"] = "Female"
            }
            else -> {
                map["usr_sex"] = "Other"
            }
        }
        map["company_type"] = "RandoxNew"
        sweet.show("Registering User")
        ApiHelper.signup(map).observe(this, { signupResponse ->
            if (signupResponse.success) {
                sweet.dismiss()
                Toast.makeText(this, signupResponse.message, Toast.LENGTH_LONG).show()
                val sharedPreferences = Shared(this)
                sharedPreferences.setString(
                    "email",
                    binding.email.text.trim().toString()
                )
                sharedPreferences.setString(
                    "password",
                    binding.password.text.trim().toString()
                )
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                sweet.dismiss()
                Toast.makeText(this, signupResponse.message, Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun openDatePicker() {

        // Build constraints.

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentDatePicked = MaterialDatePicker.todayInUtcMilliseconds()
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
            datePicker.dismiss()
        }
        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.show(supportFragmentManager, "date_picker")

    }

}