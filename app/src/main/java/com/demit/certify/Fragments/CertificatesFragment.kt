package com.demit.certify.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certify.R
import com.demit.certify.databinding.FragmentCertificatesBinding

class CertificatesFragment : Fragment() {

    lateinit var binding: FragmentCertificatesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCertificatesBinding.inflate(layoutInflater)
        return binding.root
    }

}