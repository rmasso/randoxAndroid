package com.demit.certifly.Fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.FragmentEditProfileBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class ProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var profile: TProfileModel
    lateinit var sweet:Sweet

    companion object {
        fun getInstance(profile: TProfileModel): ProfileEditFragment {
            val args = Bundle()
            args.putSerializable("profile_obj", profile)
            val profileEditFragment = ProfileEditFragment()
            profileEditFragment.arguments = args
            return profileEditFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sweet= Sweet(requireContext())
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
        profile = arguments?.getSerializable("profile_obj") as TProfileModel
        with(binding) {
            with(profile) {
                fname.setText(usr_firstname)
                dob.text = usr_birth
                usrEmail.setText(email)
                confirmEmail.setText(email)
                pnumber.setText(usr_passport)
                country.setCountryForNameCode(usr_country)
                city.setText(usr_city)
                address.setText(usr_home)
                address2.setText(usr_addressLine2)
                binding.ethnicity.setSelection(
                    resources.getStringArray(R.array.ethnicity).indexOf(ethnicity)
                )
                gender.setSelection(resources.getStringArray(R.array.genders).indexOf(usr_sex))
                zip.setText(usr_zip)
                phone.setText(usr_phone)


            }
        }
        attachClicks()

    }

    private fun attachClicks() {
        binding.updateBtn.setOnClickListener {
            if (verifyFields()) {
                updateUser()
            }
        }

        binding.dob.setOnClickListener {
            openDatePicker()
        }
    }

    private fun verifyFields(): Boolean {
        var isVerified = false
        if (binding.fname.text.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT).show()
        } else if (binding.dob.text.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Date of birth can't be empty", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.pnumber.text.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Passport Number can't be empty", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.address.text.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Address Can't be Empty", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.city.text.trim().isEmpty()) {
            Toast.makeText(requireContext(), "City can't be empty", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.zip.text.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Zip Code can't be empty", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.phone.text.trim().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Phone Number can't be empty", Toast.LENGTH_SHORT)
                .show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.usrEmail.text.trim()).matches()) {
            Toast.makeText(requireContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show()
        } else if (!binding.usrEmail.text.toString().trim().equals(binding.confirmEmail.text.toString().trim(),true)) {
            Toast.makeText(
                requireContext(),
                "Email and Confirm Email should match",
                Toast.LENGTH_SHORT
            ).show()

        } else if (binding.ethnicity.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Choose Ethnicity", Toast.LENGTH_SHORT)
                .show()
        } else if (!binding.radio.isChecked) {
            Toast.makeText(
                requireContext(),
                "Please Confirm that you agree with our Terms&conditions",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            isVerified = true
        }

        return isVerified
    }

    private fun updateUser() {

        sweet.show("Updating User")

        with(binding) {
            val sharedPref = Shared(requireContext())
            val map: MutableMap<String, String> = HashMap()
            map["usr_email"] = usrEmail.text.toString().trim()
            map["token"] = sharedPref.getString("token")
            val fullName = fname.text.toString().trim()
            val idx = fullName.lastIndexOf(' ')
            if (idx != -1) {
                map["usr_firstname"] = fullName.substring(0, idx)
                map["usr_surname"] = fullName.substring(idx + 1)

            } else {
                map["usr_firstname"] = fullName
                map["usr_surname"] = ""
            }

            map["usr_birth"] = dob.text.toString().trim()
            map["usr_home"] = address.text.toString().trim()
            map["usr_addressLine2"] = address2.text.toString().trim()
            map["usr_city"] = city.text.toString().trim()
            map["usr_zip"] = zip.text.toString().trim()
            map["usr_passport"] = pnumber.text.toString().trim()
            map["usr_country"] = country.selectedCountryNameCode
            map["usr_phone"] = phone.text.toString()
            map["usr_ethnicity"] = ethnicity.selectedItem.toString()
            map["usr_sex"] = gender.selectedItem.toString()
            map["companyName"] = "Randox"
            val isMainUser = if (sharedPref.getString("email") != profile.email) {
                map["usr_familyID"] = profile.usr_id
                false
            } else true
            postData(map, isMainUser)
        }

    }

    private fun postData(data: MutableMap<String, String>, isMainUser: Boolean) {
        ApiHelper.updateFamilyUser(data, isMainUser).observe(viewLifecycleOwner) { response ->
            if (response == "0") {
                sweet.dismiss()
                Toast.makeText(requireContext(), "User updated Successfully", Toast.LENGTH_SHORT)
                    .show()
                requireActivity().onBackPressed()
            } else {
                sweet.dismiss()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
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
            //  currentDatePicked = it

            datePicker.dismiss()
        }
        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.show(requireActivity().supportFragmentManager, "date_picker")

    }

}