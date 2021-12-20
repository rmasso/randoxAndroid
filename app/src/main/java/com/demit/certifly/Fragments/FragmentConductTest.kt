package com.demit.certifly.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.databinding.FragmentConductTestBinding

class FragmentConductTest(
    val selectedProfile: TProfileModel,
    val additionalData: Map<String, String>?,
    val plfCode:String?
) : Fragment() {
    lateinit var binding: FragmentConductTestBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_conduct_test, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        manageClicks()
    }

    private fun manageClicks() {
        binding.btnStart.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragcontainer,
                    TimerFragment(selectedProfile,additionalData,plfCode)
                )
                .addToBackStack("")
                .commit()
        }
        binding.tvSkip.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragcontainer,
                    ConfirmationFragment(selectedProfile,additionalData,null,plfCode)
                )
                .addToBackStack("")
                .commit()
        }
    }

}