package com.demit.certifly.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demit.certifly.Extras.Constants.Companion.TERMS_FIT_TO_TRAVEL
import com.demit.certifly.Extras.Constants.Companion.TERMS_UK_DAY_2
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.databinding.FragmentTestTermsBinding
class TestTermsFragment : Fragment() {
    lateinit var binding: FragmentTestTermsBinding
    lateinit var nextFragment: Fragment
    lateinit var userProfile: TProfileModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestTermsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val termsType = arguments?.getInt("test_type")
        userProfile = arguments?.getSerializable("user_profile") as TProfileModel
        termsType?.let {
            val data = when (it) {
                TERMS_FIT_TO_TRAVEL -> {
                    nextFragment = PlfFitFragment(userProfile)
                    Pair(
                        getText(R.string.fit_travel_terms_heading),
                        getText(R.string.fit_travel_terms_content)
                    )
                }
                TERMS_UK_DAY_2 -> {
                    nextFragment = DataCaptureFragment(userProfile)
                    Pair(
                        getText(R.string.uk_day2_terms_heading),
                        getText(R.string.uk_day2_terms_content)
                    )
                }
                else -> Pair("", "")
            }
            with(binding) {
                termsHeading.text = data.first
                termsContent.text = data.second
            }
        }

        attachClicks()
    }

    fun attachClicks() {
        with(binding) {
            btnCancel.setOnClickListener {
                requireActivity().onBackPressed()
            }
            btnAgree.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragcontainer, nextFragment)
                    .addToBackStack("")
                    .commit()
            }

        }
    }


}