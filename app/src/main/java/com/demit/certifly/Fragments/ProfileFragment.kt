package com.demit.certifly.Fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import com.demit.certifly.Extras.*
import com.demit.certifly.Extras.PermissionUtil.isMarshMallowOrAbove
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.FragmentProfileBinding
import com.demit.certifly.databinding.ViewProfileBinding
import com.demit.certifly.extensions.toBase64String
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.BlinkIdUISettings
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_profile.view.*

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var adapter: ProfileAdapter
    var index = 0
    var list: MutableList<TProfileModel> = ArrayList()

    lateinit var sweet: Sweet

    //Blink Id Variables
    private lateinit var mRecognizer: BlinkIdCombinedRecognizer
    private lateinit var mRecognizerBundle: RecognizerBundle
    private val MY_REQUEST_CODE = 801
    private var isFromAdapterClick: Boolean = false

    //Camera Permission Variables
    lateinit var cameraRequestLauncher: ActivityResultLauncher<String>
    var enable = false
    private val CAMERA_REQUEST_CODE = 1001


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        sweet = Sweet(requireContext())



        getProfiles()
        adapter = ProfileAdapter()
        binding.rv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicks()
        enable =
            requireActivity().getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)
                .getBoolean("should_take_to_settings", false)
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


        mRecognizer = BlinkIdCombinedRecognizer()
        mRecognizer.setReturnFaceImage(true)
        mRecognizer.setReturnFullDocumentImage(true)
        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = RecognizerBundle(mRecognizer)
        binding.gender.setSelection(0)
        binding.ethnicity.setSelection(0)
        shouldEnableFormFields(false)

        if (isMarshMallowOrAbove()) {
            cameraRequestLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        requireActivity().getSharedPreferences(
                            "app",
                            AppCompatActivity.MODE_PRIVATE
                        ).edit()
                            .putBoolean("should_take_to_settings", false).apply()
                        startScanning()
                    } else {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            if (!shouldShowRequestPermissionRationale(PermissionUtil.CAMERA_PERMISSION)) {
                                enable = true
                                requireActivity().getSharedPreferences(
                                    "app",
                                    AppCompatActivity.MODE_PRIVATE
                                ).edit()
                                    .putBoolean("should_take_to_settings", true).apply()
                            }

                        } else {
                            //Android 11 and up
                            enable = true
                            requireActivity().getSharedPreferences(
                                "app",
                                AppCompatActivity.MODE_PRIVATE
                            ).edit()
                                .putBoolean("should_take_to_settings", true).apply()
                        }

                    }

                }
        }
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
                    val p = TProfileModel()
                    p.usr_sex = resources.getStringArray(R.array.genders)[0]
                    p.ethnicity = resources.getStringArray(R.array.ethnicity)[0]
                    shouldEnableFormFields(true)
                    val docImage = result.fullDocumentFrontImage?.convertToBitmap()
                    val mrzResult = result.mrzResult
                    docImage?.let {
                        p.usr_image = it.toBase64String()
                    }


                    mrzResult?.let { mrzCodeResult ->
                        val fullName = "${mrzCodeResult.secondaryId} ${mrzCodeResult.primaryId}"

                        val dateOfBirth = with(mrzCodeResult.dateOfBirth.date) {
                            String.format("%02d-%02d-%04d", this?.day, this?.month, this?.year)
                        }
                        val passport = mrzCodeResult.documentNumber
                        with(p) {
                            usr_firstname = fullName
                            usr_birth = dateOfBirth
                            usr_passport = passport
                        }
                    }
                    list.add(p)

                    setData(list.size - 1)



                    binding.rv.adapter?.itemCount?.minus(1)?.let { it1 ->
                        binding.rv.smoothScrollToPosition(
                            it1
                        )
                    }


                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (PermissionUtil.hasCameraPermission(requireContext())) {
                requireActivity().getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE).edit()
                    .putBoolean("should_take_to_settings", false).apply()
                startScanning()
            }
        }

    }


    fun clicks() {
        if (isMarshMallowOrAbove()) {
            binding.scroll.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                // the delay of the extension of the FAB is set for 12 items
                if (scrollY > oldScrollY + 12 && binding.editBtn.isExtended) {
                    binding.editBtn.shrink()
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !binding.editBtn.isExtended) {
                    binding.editBtn.extend()
                }

                // if the nestedScrollView is at the first item of the list then the
                // extended floating action should be in extended state
                if (scrollY == 0) {
                    binding.editBtn.extend()
                }
            }
        }
        view?.let { view ->
            with(view) {
                isFocusableInTouchMode = true
                requestFocus()
                setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_UP && keyCode == KEYCODE_BACK) {
                        if (binding.passportScanInfo.visibility == View.VISIBLE) {
                            binding.passportScanInfo.visibility = View.GONE
                            shouldShowEditFab(true)
                            false
                        } else {
                            requireActivity().onBackPressed()
                        }
                        true

                    } else
                        false

                }
            }
        }

        binding.add.setOnClickListener() {
            binding.passportScanInfo.visibility = View.VISIBLE
            shouldShowEditFab(false)
        }

        binding.dob.setOnClickListener {
            openDatePicker()
        }

        binding.editBtn.setOnClickListener {
            val editFragment = ProfileEditFragment.getInstance(list[index])
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragcontainer,
                    editFragment
                )
                .addToBackStack("")
                .commit()
        }

        binding.save.setOnClickListener {
            Log.d("++index", list[index].usr_image)
            if (list[index].usr_image == "") {
                if (context != null)
                    Toast.makeText(context, "Please Scan Passport", Toast.LENGTH_SHORT).show()
            } else if (binding.fname.text.trim().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Firstname can't be empty", Toast.LENGTH_SHORT).show()
            } else if (binding.dob.text.trim().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Date of birth can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.pnumber.text.trim().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Passport Number can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.address.text.trim().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Address Can't be Empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.city.text.trim().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "City can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.zip.text.trim().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Zip Code can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.phone.text.trim().toString().isEmpty()) {
                if (context != null)
                    Toast.makeText(context, "Phone Number can't be empty", Toast.LENGTH_SHORT)
                        .show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.trim()).matches()) {
                if (context != null)
                    Toast.makeText(context, "Invalid Email Address", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.confirmEmail.text.trim())
                    .matches()
            ) {
                if (context != null)
                    Toast.makeText(context, "Invalid Confirm Email Address", Toast.LENGTH_SHORT)
                        .show()
            } else if (binding.email.text.trim() != binding.confirmEmail.text.trim()) {
                if (context != null)
                    Toast.makeText(
                        context,
                        "Email and Confirm Email should match",
                        Toast.LENGTH_SHORT
                    ).show()

            } else if (binding.ethnicity.selectedItemPosition == 0) {
                if (context != null)
                    Toast.makeText(context, "Choose Ethnicity", Toast.LENGTH_SHORT)
                        .show()
            } else if (!binding.radio.isChecked) {
                Toast.makeText(
                    context,
                    "Please Confirm that you agree with our Terms&conditions",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                familyregister()
            }
        }

        binding.subInfo.verify.setOnClickListener {
            //startScanning()
            if (isMarshMallowOrAbove()) {
                requestCameraPermission()
            } else {
                startScanning()
            }
        }
        binding.subInfo.cancel.setOnClickListener {
            binding.passportScanInfo.visibility = View.GONE
            shouldShowEditFab(true)
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



        list[index].usr_sex =
            resources.getStringArray(R.array.genders)[binding.gender.selectedItemPosition]

        binding.gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list[index].usr_sex = resources.getStringArray(R.array.genders)[position]

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

        if (list[position].usr_id != "") {
            binding.radio.isChecked = true
            binding.radio.isClickable = false
        } else {
            binding.radio.isChecked = false
            binding.radio.isClickable = true
        }


        binding.fname.setText(list[position].usr_firstname)
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

        binding.gender.setSelection(
            resources.getStringArray(R.array.genders).indexOf(list[position].usr_sex)
        )
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
                val enable = model.usr_id == ""
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

    private fun requestCameraPermission() {
        when {
            PermissionUtil.hasCameraPermission(requireContext()) -> {
                startScanning()
            }
            enable -> {
                val permissionSettingsDialog =
                    PermissionInfoDialog(
                        true,
                        Constants.DIALOG_TYPE_CAMERA_PASSPORT
                    ) { btnClickId, dialog ->
                        when (btnClickId) {
                            R.id.btn_settings -> {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                val uri =
                                    Uri.fromParts("package", requireContext().packageName, null);
                                intent.data = uri;
                                startActivityForResult(intent, 1001)
                                dialog.dismiss()
                            }
                            R.id.btn_not_now -> {
                                dialog.dismiss()
                            }
                        }
                    }
                permissionSettingsDialog.isCancelable = false
                permissionSettingsDialog.show(
                    requireActivity().supportFragmentManager,
                    "permission_dialog"
                )


            }
            else -> {
                //Show some cool ui to the user explaining why we use this permission
                val permissionDialog =
                    PermissionInfoDialog(
                        false,
                        Constants.DIALOG_TYPE_CAMERA_PASSPORT
                    ) { btnClickId, dialog ->
                        when (btnClickId) {
                            R.id.btn_allow -> {
                                cameraRequestLauncher.launch(PermissionUtil.CAMERA_PERMISSION)
                                dialog.dismiss()
                            }
                            R.id.btn_not_now -> {
                                dialog.dismiss()
                            }
                        }
                    }
                permissionDialog.isCancelable = false
                permissionDialog.show(requireActivity().supportFragmentManager, "permission_dialog")

            }
        }
    }

    fun familyregister() {
        if (list[index].usr_id != "") {
            Toast.makeText(
                requireContext(),
                "Edit Family user under development",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        sweet.show("Registering User")


        val map: MutableMap<String, String> = HashMap()
        map["usr_email"] = list[index].email.trim()
        map["token"] = Shared(requireContext()).getString("token")
        val fullName = list[index].usr_firstname.trim()
        val idx = fullName.lastIndexOf(' ')
        if (idx != -1) {
            map["usr_firstname"] = fullName.substring(0, idx)
            map["usr_surname"] = fullName.substring(idx + 1)

        } else {
            map["usr_firstname"] = fullName
            map["usr_surname"] = ""
        }

        map["usr_birth"] = list[index].usr_birth.trim()
        map["usr_home"] = list[index].usr_home.trim()
        map["usr_addressLine2"] = list[index].usr_addressLine2.trim()
        map["usr_city"] = list[index].usr_city.trim()
        map["usr_zip"] = list[index].usr_zip.trim()
        map["usr_passport"] = list[index].usr_passport.trim()
        map["usr_country"] = list[index].usr_country.trim()
        map["usr_phone"] = list[index].usr_phone.trim()
        map["usr_passport_image"] = list[index].usr_image
        map["usr_ethnicity"] = list[index].ethnicity
        map["usr_sex"] =
            resources.getStringArray(R.array.genders)[binding.gender.selectedItemPosition]
        map["companyName"] = "Randox"
        ApiHelper.postFamilyUser(map).observe(viewLifecycleOwner) { response ->
            sweet.dismiss()
            if (response == "0") {
                Toast.makeText(
                    context,
                    "User registered successfully",
                    Toast.LENGTH_SHORT
                ).show()
                getProfiles()
                shouldEnableFormFields(false)
            } else {
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    fun getProfiles() {
        sweet.show("Getting profiles")

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
                            for (item in list)
                                item.usr_firstname += " ${item.usr_surname}"

                            index = 0
                            setData(index)
                            list[0].checked = true;
                        }
                        adapter.notifyDataSetChanged()
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
            list[index].usr_birth = textDate
            //  currentDatePicked = it

            datePicker.dismiss()
        }
        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.show(requireActivity().supportFragmentManager, "date_picker")

    }

    private fun shouldEnableFormFields(enable: Boolean) {
        with(binding) {
            fname.isEnabled = enable
            dob.isEnabled = enable
            val color = if (enable)
                ContextCompat.getColor(requireContext(), R.color.black)
            else
                ContextCompat.getColor(requireContext(), R.color.textHint)
            dob.setTextColor(color)

            pnumber.isEnabled = enable
            phone.isEnabled = enable
            country.setCcpClickable(enable)
            gender.isEnabled = enable
            ethnicity.isEnabled = enable
            address.isEnabled = enable
            city.isEnabled = enable
            zip.isEnabled = enable
            email.isEnabled = enable
            confirmEmail.isEnabled = enable
            address2.isEnabled = enable

            val shouldHideSaveBtn = if (!enable) View.GONE else View.VISIBLE
            save.visibility = shouldHideSaveBtn
            shouldShowEditFab(!enable)
        }
    }

    private fun shouldShowEditFab(show: Boolean) {
        binding.editBtn.visibility = if (show) View.VISIBLE else View.GONE
    }
}