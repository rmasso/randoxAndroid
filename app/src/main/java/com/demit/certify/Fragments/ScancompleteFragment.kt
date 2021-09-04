package com.demit.certify.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certify.R
import com.demit.certify.databinding.FragmentScancompleteBinding


class ScancompleteFragment : Fragment() {
    lateinit var binding: FragmentScancompleteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScancompleteBinding.inflate(layoutInflater)
        setclicks()
        return binding.root
    }
    fun setclicks(){
        binding.submit.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragcontainer,ScansubmitFragment())?.addToBackStack("")?.commit()
        }
    }
}