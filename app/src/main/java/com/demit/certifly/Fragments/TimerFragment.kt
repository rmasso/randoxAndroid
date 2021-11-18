package com.demit.certifly.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demit.certifly.Extras.DetachableClickListener
import com.demit.certifly.Extras.Timer
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.databinding.FragmentTimerBinding
import kotlinx.android.synthetic.main.fragment_confirmation.*
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment(val selectedProfile: TProfileModel,val additionalData: Map<String, String>?) : Fragment() {
    lateinit var binding: FragmentTimerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_timer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val timeOnWhichTimerStarted= Date().time

            Timer.startTimer().observe(viewLifecycleOwner, {
            it?.let { millis ->
                if (millis == 0L) {
                    with(binding) {
                        timeText.text = String.format("%02d:%02d", 0, 0)
                        timeProgress.setProgress(100f, true)
                        val dateFormatGmt =  SimpleDateFormat("HH:mm:ss")
                        dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
                        val gmtTimeString= dateFormatGmt.format(timeOnWhichTimerStarted)

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragcontainer,
                                ConfirmationFragment(selectedProfile,additionalData,gmtTimeString)
                            )
                            .commit()
                    }
                } else {
                    val totalSeconds = millis / 1000
                    val secondInMinuteRange = totalSeconds % 60
                    val minute = (totalSeconds / 60) % 60
                    binding.timeText.text =
                        String.format("%02d:%02d", minute.toInt(), secondInMinuteRange.toInt())
                    if (secondInMinuteRange == 0L) {
                        val progressPercent = (100 - ((millis.toFloat() / Timer.MAX_TIME) * 100))
                        binding.timeProgress.setProgress(progressPercent, true)
                    }
                }
            }
        })

     binding.btnCancel.setOnClickListener {
         showAlertDialog()
     }


    }

    private fun showAlertDialog() {

        val positiveClickListener = DetachableClickListener.wrap { _, _ ->
          requireActivity().onBackPressed()
        }

        val negativeClickListener = DetachableClickListener.wrap { _, _ ->  }


        val builder = AlertDialog.Builder(requireActivity())
            .setTitle("Skip timer")
            .setMessage("Are you sure you want to quit the test.")
            .setPositiveButton("Yes", positiveClickListener)
            .setNegativeButton("No", negativeClickListener)
            .create()

        //avoid memory leaks
        positiveClickListener.clearOnDetach(builder)
        negativeClickListener.clearOnDetach(builder)
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timer.stopTimer()
    }
}