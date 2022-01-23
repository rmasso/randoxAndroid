package com.demit.certifly.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demit.certifly.databinding.FragmentFaqBinding

import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.demit.certifly.R
import com.demit.certifly.adapters.VideoSliderAdapter
import com.google.android.material.tabs.TabLayoutMediator


class FaqFragment : Fragment() {
    lateinit var binding: FragmentFaqBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFaqBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSlider()
        binding.faqText.movementMethod = LinkMovementMethod()

    }

    private fun setUpSlider() {
        val totalPages = resources.getStringArray(R.array.faq_video_ids).size
        binding.slider.adapter =
            VideoSliderAdapter(requireActivity() as AppCompatActivity, totalPages)

        TabLayoutMediator(binding.pageIndicator, binding.slider)
        { _, _ ->}.attach()
    }


}