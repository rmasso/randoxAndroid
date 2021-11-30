package com.demit.certifly.Fragments

import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certifly.Extras.Global
import com.demit.certifly.databinding.FragmentScansubmitBinding

class ScansubmitFragment : Fragment() {
    lateinit var binding: FragmentScansubmitBinding;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScansubmitBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnHome.setOnClickListener {
            Global.should_go_home = true
            requireActivity().onBackPressed()
        }
    }

}