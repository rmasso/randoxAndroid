package com.demit.certifly.Fragments

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.FragmentDatacaptureBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.hbb20.CountryCodePicker
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DataCaptureFragment(val selectedProfile: TProfileModel) : Fragment() {

    private lateinit var binding: FragmentDatacaptureBinding

    // private lateinit var countryCodePicker: CountryCodePicker
    private var isSelected = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_datacapture,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnProceed.setOnClickListener {
            validateForm()
        }

        binding.dateOfArrivalText.setOnClickListener {
            openDatePicker(it as TextView, "Select date of arrival")
        }

        /* binding.lastDateDepartedText.setOnClickListener {
             openDatePicker(it as TextView, "Select last date of departure")
         }*/

        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.bookingRefEdit.filters = binding.bookingRefEdit.filters + InputFilter.AllCaps()

        /* binding.transitedPickerContainer.setOnClickListener {
             countryCodePicker.launchCountrySelectionDialog()

         }*/

        //initCounterDialogPicker()
        binding.receivedVaccine.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1) {
                        val vaccineNameAdapter = getSpinnerAdapter(R.array.none_opt_arr)
                        val yesNoAdapter = getSpinnerAdapter(R.array.none_opt_arr)
                        binding.vaccinePicker.adapter = vaccineNameAdapter
                        binding.yesNoPicker.adapter = yesNoAdapter
                        binding.vaccinePicker.isEnabled = false
                        binding.yesNoPicker.isEnabled = false
                        isSelected = true
                    } else {
                        if (isSelected) {
                            val vaccineNameAdapter = getSpinnerAdapter(R.array.vaccines)
                            val yesNoAdapter = getSpinnerAdapter(R.array.days_past_yes_no)
                            binding.vaccinePicker.adapter = vaccineNameAdapter
                            binding.yesNoPicker.adapter = yesNoAdapter
                            binding.vaccinePicker.isEnabled = true
                            binding.yesNoPicker.isEnabled = true
                            isSelected = false
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }


    }

    private fun validateForm() {
        with(binding) {
            if (receivedVaccine.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Please confirm that you have received your vaccine",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (vaccinePicker.selectedItemPosition == 0 && vaccinePicker.selectedItem.toString() != "None") {
                Toast.makeText(
                    requireContext(),
                    "Vaccine Name missing",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (yesNoPicker.selectedItemPosition == 0 && yesNoPicker.selectedItem.toString() != "None") {
                Toast.makeText(
                    requireContext(),
                    "Please confirm that either Yes or No that it has been 14 or more days since you have been fully vaccinated in the UK-approved program.",
                    Toast.LENGTH_LONG
                ).show()

            } else if (addressLine1Edit.text.trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Missing isolation address line1",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (cityEdit.text.trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "City or Town is missing",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (zipEdit.text.trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Postal code is missing",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (dateOfArrivalText.text.trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Date of arrival is missing",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (travelTypePicker.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Transport type is missing.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (flightNumberEdit.text.trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Flight/ Vessel/ Train number is missing",
                    Toast.LENGTH_SHORT
                ).show()
            } /*else if (lastDateDepartedText.text.trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Last date of departure from or through a non-exempt country or territory is missing",
                    Toast.LENGTH_LONG
                ).show()
            }*/ else if (bookingRefEdit.text.trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "PLF number is missing",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                proceed()
            }


        }
    }

    private fun proceed() {
        val dataMap = HashMap<String, String>()
        with(binding) {
            dataMap["cert_nationality"] = country.selectedCountryEnglishName
            dataMap["is_viccinated"] = receivedVaccine.selectedItem.toString()
            dataMap["vaccine_name"] = vaccinePicker.selectedItem.toString()
            dataMap["is_fully_vaccinated_14days_uk"] = yesNoPicker.selectedItem.toString()
            dataMap["two_day_booking_ref"] = bookingRefEdit.text.toString().trim()
            dataMap["isolation_address_line1"] = addressLine1Edit.text.toString().trim()
            dataMap["isolation_address_line2"] = addressLine2Edit.text.toString().trim()
            dataMap["town"] = cityEdit.text.toString().trim()
            dataMap["post_code"] = zipEdit.text.toString().trim()
            dataMap["arrival_date"] = dateOfArrivalText.text.toString().trim()
            dataMap["fligh_vessel_train_no"] = flightNumberEdit.text.toString().trim()
            dataMap["nhs_no"] = ""//nhsNumberEdit.text.toString().trim()
            dataMap["country_territory_part_journey"] = ""//transitedText.text.toString().trim()
            dataMap["last_date_department"] = ""//lastDateDepartedText.text.toString().trim()
            dataMap["Country_of_departure"] = departurePicker.selectedCountryEnglishName
            dataMap["transport_type"] = travelTypePicker.selectedItem.toString()
        }

        val sweet = Sweet(requireContext())
        sweet.show("Verifying")
        ApiHelper.verifyPlfNumber(
            Shared(requireContext()).getString("token"),
            binding.bookingRefEdit.text.toString()
        )
            .observe(viewLifecycleOwner) { validityResponse ->
                if (validityResponse == "0") {
                    sweet.dismiss()

                    /*requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragcontainer, ScancompleteFragment(selectedProfile, dataMap))
                        .addToBackStack("")
                        .commit()*/

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragcontainer,
                            FragmentConductTest(selectedProfile,dataMap)
                        )
                        .addToBackStack("")
                        .commit()

                } else {
                    sweet.dismiss()
                    if (validityResponse == "100")
                        Toast.makeText(
                            requireContext(),
                            "Invalid booking reference number",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(requireContext(), validityResponse, Toast.LENGTH_SHORT)
                            .show()
                }
            }


    }

    private fun openDatePicker(textView: TextView, title: String) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentDatePicked = MaterialDatePicker.todayInUtcMilliseconds()
        calendar.timeInMillis = currentDatePicked
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setEnd(calendar.timeInMillis)
                .setValidator(DateValidatorPointBackward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(currentDatePicked)
            .build()
        datePicker.addOnPositiveButtonClickListener {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            val textDate = dateFormat.format(Date(it))
            textView.text = textDate
            datePicker.dismiss()
        }
        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.show(requireActivity().supportFragmentManager, "date_picker")

    }

    /*   private fun openTimePicker() {
           *//*val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select time of arrival")
                .build()

        timePicker.addOnPositiveButtonClickListener {
            val hourString = String.format("%02d", timePicker.hour)
            val minuteString = String.format("%02d", timePicker.minute)
            val amOrPm = if (timePicker.hour >= 12) "PM" else "AM"
            binding.timeOfArrivalText.text = "$hourString:$minuteString $amOrPm"
            timePicker.dismiss()
        }
        timePicker.addOnCancelListener {
            timePicker.dismiss()
        }
        timePicker.show(requireActivity().supportFragmentManager, "timepicker")*//*
    }

    private fun initCounterDialogPicker() {
        *//*countryCodePicker = CountryCodePicker(requireContext())

        countryCodePicker.setOnCountryChangeListener {
            countryCodePicker.selectedCountryEnglishName?.let {
               // binding.transitedText.text = it
            }

        }*//*


    }*/

    private fun getSpinnerAdapter(resourceId: Int): ArrayAdapter<CharSequence> =
        ArrayAdapter.createFromResource(
            requireContext(),
            resourceId,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }


}