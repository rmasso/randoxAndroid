package com.demit.certify.Fragments

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demit.certify.Extras.Shared
import com.demit.certify.Extras.Sweet
import com.demit.certify.Models.TProfileModel
import com.demit.certify.R
import com.demit.certify.data.ApiHelper
import com.demit.certify.databinding.FragmentDatacaptureBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DataCaptureFragment(val selectedProfile: TProfileModel) : Fragment() {

    private lateinit var binding: FragmentDatacaptureBinding
    private var currentDatePicked: Long = MaterialDatePicker.todayInUtcMilliseconds()


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

        binding.lastDateDepartedText.setOnClickListener {
            openDatePicker(it as TextView, "Select last date of departure")
        }

        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.bookingRefEdit.filters = binding.bookingRefEdit.filters + InputFilter.AllCaps()


    }

    private fun validateForm() {
        with(binding) {
            if (receivedVaccine.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Please confirm that you have received your vaccine",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (vaccinePicker.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Vaccine Name missing",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (yesNoPicker.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Please confirm that either Yes or No that it has been 14 or more days since you have been fully vaccinated in the UK-approved program.",
                    Toast.LENGTH_LONG
                ).show()

            } else if (addressLine1Edit.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Missing isolation address line1",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (cityEdit.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "City or Town is missing",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (zipEdit.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Postal code is missing",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (dateOfArrivalText.text.isEmpty()) {
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
            } else if (flightNumberEdit.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Flight/ Vessel/ Train number is missing",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (lastDateDepartedText.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Last date of departure from or through a non-exempt country or territory is missing",
                    Toast.LENGTH_LONG
                ).show()
            } else if (bookingRefEdit.text.isEmpty()) {
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
            dataMap["is_viccinated"] =
                resources.getStringArray(R.array.vaccines_types)[receivedVaccine.selectedItemPosition]
            dataMap["vaccine_name"] =
                resources.getStringArray(R.array.vaccines)[vaccinePicker.selectedItemPosition]
            dataMap["is_fully_vaccinated_14days_uk"] =
                resources.getStringArray(R.array.days_past_yes_no)[yesNoPicker.selectedItemPosition]
            dataMap["two_day_booking_ref"] = bookingRefEdit.text.toString()
            dataMap["isolation_address_line1"] = addressLine1Edit.text.toString()
            dataMap["isolation_address_line2"] = addressLine2Edit.text.toString()
            dataMap["town"] = cityEdit.text.toString()
            dataMap["post_code"] = zipEdit.text.toString()
            dataMap["arrival_date"] = dateOfArrivalText.text.toString()
            dataMap["fligh_vessel_train_no"] = flightNumberEdit.text.toString()
            dataMap["nhs_no"] = nhsNumberEdit.text.toString()
            dataMap["country_territory_part_journey"] = transitedPicker.selectedCountryEnglishName
            dataMap["last_date_department"] = lastDateDepartedText.text.toString()
            dataMap["Country_of_departure"] = departurePicker.selectedCountryEnglishName
            dataMap["transport_type"] =
                resources.getStringArray(R.array.travel_type)[travelTypePicker.selectedItemPosition]
        }

        val sweet = Sweet(requireContext())
        sweet.show("Verifying...")
        ApiHelper.verifyPlfNumber(
            Shared(requireContext()).getString("token"),
            binding.bookingRefEdit.text.toString()
        )
            .observe(viewLifecycleOwner) { validityResponse ->
                if (validityResponse == "0") {
                    sweet.dismiss()

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragcontainer, ScancompleteFragment(selectedProfile, dataMap))
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
            currentDatePicked = it

            datePicker.dismiss()
        }
        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.show(requireActivity().supportFragmentManager, "date_picker")

    }

    private fun openTimePicker() {
        /*val timePicker =
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
        timePicker.show(requireActivity().supportFragmentManager, "timepicker")*/
    }
}