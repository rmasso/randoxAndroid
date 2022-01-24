package com.demit.certifly.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.demit.certifly.Fragments.VideoFragment

class VideoSliderAdapter(val activity: AppCompatActivity, private val totalPages: Int) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = totalPages

    override fun createFragment(position: Int): Fragment = VideoFragment.getInstance(position)
}