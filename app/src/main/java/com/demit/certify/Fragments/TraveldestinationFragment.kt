package com.demit.certify.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demit.certify.R
import com.demit.certify.databinding.FragmentTraveldestinationBinding


class TraveldestinationFragment : Fragment() {
    lateinit var binding: FragmentTraveldestinationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTraveldestinationBinding.inflate(layoutInflater)

        clicks()
        return binding.root
    }

    private fun clicks() {
        binding.ready.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragcontainer,TesterFragment())?.commit()
        }
    }

}