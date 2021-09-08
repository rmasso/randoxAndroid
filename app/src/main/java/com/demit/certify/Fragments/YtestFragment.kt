package com.demit.certify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certify.Interfaces.DashboardInterface
import com.demit.certify.R
import com.demit.certify.databinding.FragmentYtestBinding


class YtestFragment(val dInterface : DashboardInterface) : Fragment() {

    lateinit var binding : FragmentYtestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentYtestBinding.inflate(layoutInflater)


        clicks()
        return binding.root
    }

    private fun clicks() {

        binding.sw1.setOnClickListener {
            if(binding.sw1.isChecked && binding.sw2.isChecked){
                binding.ready.visibility = View.VISIBLE
            }else{
                binding.ready.visibility = View.GONE
            }
        }
        binding.sw2.setOnClickListener {
            if(binding.sw1.isChecked && binding.sw2.isChecked){
                binding.ready.visibility = View.VISIBLE
            }else{
                binding.ready.visibility = View.GONE
            }
        }
        binding.ready.setOnClickListener {
            if(binding.sw1.isChecked && binding.sw2.isChecked){
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragcontainer,TraveldestinationFragment())?.addToBackStack("")?.commit()
            }

        }

        binding.cancel.setOnClickListener {
            dInterface.setpage(0)
        }
    }

    override fun onStart() {
        super.onStart()
        if(binding.sw1.isChecked && binding.sw2.isChecked){
            binding.ready.visibility = View.VISIBLE
        }else{
            binding.ready.visibility = View.GONE
        }
    }
}