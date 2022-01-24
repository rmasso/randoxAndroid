package com.demit.certifly.Fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.demit.certifly.Extras.Constants
import com.demit.certifly.R
import com.demit.certifly.databinding.FragmentVideoSliderBinding

class VideoFragment : Fragment(), View.OnClickListener {
    companion object {
        private const val ARG_POSITION = "ARG_POSITION"
        fun getInstance(position: Int) = VideoFragment().apply {
            arguments = bundleOf(ARG_POSITION to position)
        }
    }

    lateinit var binding: FragmentVideoSliderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoSliderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        attachClicks()
        val position = requireArguments().getInt(ARG_POSITION)
        val currentVideoThumbnail = getVideoThumbnails()[position]
        binding.thumbnailImg.setImageResource(currentVideoThumbnail)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.video_card -> {
                launchYoutube()
            }
        }
    }

    private fun attachClicks() {
        binding.videoCard.setOnClickListener(this)
    }

    private fun getVideoThumbnails(): List<Int> =
        ArrayList<Int>().apply {
            add(R.drawable.download_app)
            add(R.drawable.create_account)
            add(R.drawable.scan_a_new_test)
            add(R.drawable.add_family_member)
        }

    private fun launchYoutube() {
        val videoId = resources.getStringArray(R.array.faq_video_ids)[requireArguments().getInt(ARG_POSITION)]
        val appIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${videoId}"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "http://www.youtube.com/watch?v=${videoId}"
            )
        )
        try {
            requireContext().startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            requireContext().startActivity(webIntent)
        }
    }


}