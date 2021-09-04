package com.demit.certify.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certify.R
import com.demit.certify.databinding.FragmentTesterBinding

class TesterFragment : Fragment() {
    lateinit var binding : FragmentTesterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTesterBinding.inflate(layoutInflater)
        setclicks()
        return binding.root
    }

    fun setclicks(){
        binding.rd1.setOnClickListener {
            setraddio(it)
        }
        binding.rd3.setOnClickListener {
            setraddio(it)
        }
        binding.rd2.setOnClickListener {
            setraddio(it)
        }
        binding.rd4.setOnClickListener {
            setraddio(it)
        }
    }
    fun setraddio(view: View){
        binding.rd1.isChecked = false
        binding.rd2.isChecked = false
        binding.rd3.isChecked = false
        binding.rd4.isChecked = false

        when (view) {
            binding.rd1 -> {
                binding.rd1.isChecked = true
            }
            binding.rd2 -> {
                binding.rd2.isChecked = true
            }
            binding.rd3 -> {
                binding.rd3.isChecked = true
            }
            binding.rd4 -> {
                binding.rd4.isChecked = true
            }
        }
    }
}