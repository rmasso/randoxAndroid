package com.demit.certifly.Fragments

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.databinding.FragmentConfirmationBinding
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint

import java.text.SimpleDateFormat
import java.util.*

class ConfirmationFragment(
    val selectedProfile: TProfileModel,
    val additionalData: Map<String, String>?,
    val time: String?
) : Fragment() {
    lateinit var binding: FragmentConfirmationBinding
    val currentDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
    lateinit var timePickerDialog: TimePickerDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_confirmation,
            container,
            false
        )
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        manageClicks()
        binding.testTakerContent.text =
            "${selectedProfile.usr_firstname} ${selectedProfile.usr_surname}"
        binding.dateContent.text = currentDate
        time?.let {
            binding.timeContent.text = it
        } ?: run {
            binding.timeContent.isEnabled = true
            val now = Calendar.getInstance()
            timePickerDialog = TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
                binding.timeContent.text =
                    String.format("%02d:%02d:%02d", hourOfDay, minute, second)
            }, now[Calendar.HOUR_OF_DAY], now[Calendar.MINUTE], now[Calendar.SECOND], true)
        }


    }

    private fun manageClicks() {
        binding.btnNext.setOnClickListener {
            if (binding.timeContent.text == "") {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.confirm_time),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!binding.radio.isChecked) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.confirm_test),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragcontainer, ScancompleteFragment(
                            selectedProfile, additionalData,
                            "${binding.dateContent.text} ${binding.timeContent.text}"
                        )
                    )
                    .addToBackStack("")
                    .commit()
            }
        }

        binding.timeContent.setOnClickListener {
            openTimePicker()
        }
    }

    private fun openTimePicker() {
        val max = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        val min = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        min.timeInMillis = max.timeInMillis - 1800000

        timePickerDialog.setMaxTime(Timepoint(max[Calendar.HOUR_OF_DAY], max[Calendar.MINUTE]))

        timePickerDialog.setMinTime(
            min[Calendar.HOUR_OF_DAY],
            min[Calendar.MINUTE],
            min[Calendar.SECOND]
        )


        timePickerDialog.show(requireActivity().supportFragmentManager, "Time Picker")
    }

}