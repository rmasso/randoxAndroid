package com.demit.certifly.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certifly.Interfaces.DashboardInterface
import com.demit.certifly.databinding.FragmentHomeBinding

class HomeFragment(val dinterface: DashboardInterface) : Fragment() {

    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        Log.d("sss", "1");
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
        binding.privacy.setOnClickListener {
            openBrowser()
        }
    }

    private fun openBrowser() {
        val url = "https://www.randoxhealth.com/randox-certifly-privacy-policy"
        val privacyIntent = Intent(Intent.ACTION_VIEW);
        privacyIntent.data = Uri.parse(url);
        startActivity(privacyIntent)
    }

}