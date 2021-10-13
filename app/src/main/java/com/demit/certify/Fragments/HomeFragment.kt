package com.demit.certify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certify.Interfaces.DashboardInterface
import com.demit.certify.R
import com.demit.certify.databinding.FragmentHomeBinding

class HomeFragment(val dinterface : DashboardInterface) : Fragment() {

    lateinit var binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        Log.d("sss" , "1");
        clicks()
        return binding.root
    }

    private fun clicks() {
        binding.activecert.setOnClickListener {
            dinterface.setpage(1)
        }
        binding.register.setOnClickListener {
            dinterface.setpage(2)
        }
        binding.faqs.setOnClickListener{
            dinterface.setpage(3)
        }
        binding.profile.setOnClickListener {
            dinterface.setpage(4)
        }
    }

}