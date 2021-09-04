package com.demit.certify.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certify.R
import com.demit.certify.databinding.FragmentScansubmitBinding

class ScansubmitFragment : Fragment() {
    lateinit var binding : FragmentScansubmitBinding;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScansubmitBinding.inflate(layoutInflater)

        return binding.root
    }

}