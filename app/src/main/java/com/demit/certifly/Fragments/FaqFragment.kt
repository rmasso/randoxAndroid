package com.demit.certifly.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demit.certifly.Interfaces.FaqScrollInterface
import com.demit.certifly.Models.FaqAsk
import com.demit.certifly.adapters.FaqAdapter
import com.demit.certifly.databinding.FragmentFaqBinding
import android.content.ActivityNotFoundException

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.demit.certifly.Extras.Constants
import com.demit.certifly.R


class FaqFragment : Fragment(), FaqScrollInterface {
    lateinit var binding: FragmentFaqBinding
    lateinit var adapter: FaqAdapter
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
        initAdapter()
        initRecyclerView()
        Glide.with(requireContext())
            .load(R.drawable.thumbnail3)
            .placeholder(R.drawable.thumbnail3)
            .into(binding.youtubePlayer)

        binding.youtubePlayer.setOnClickListener {
            launchYoutube()
        }
    }

    private fun initRecyclerView() {
        with(binding) {
            this.rvFaq.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.rvFaq.isNestedScrollingEnabled = false
            this.rvFaq.adapter = adapter

        }

    }

    private fun initAdapter() {
        adapter = FaqAdapter(getSampleList(), this)
    }

    private fun getSampleList(): List<FaqAsk> {
        val faqAskList = ArrayList<FaqAsk>()


        faqAskList.add(
            FaqAsk(
                question = "How do I use the COVID-19 sample collection kit?",
                answer = "Each sample collection kit contains instructions for use. It is the user’s responsibility to ensure the instructions are read in full prior to engaging in the sampling process."
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "Will I get a travel certificate?",
                answer = "You will receive your results via email, as well as a certificate of your results to use whilst travelling. In order for your certificate to be correct, please ensure the information used during the registration process matches the details on your passport"
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "Do I need to prepare for my sample?",
                answer = "Food or drink should not be consumed within the 30 minutes"
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "What does it mean if I have a negative result?",
                answer = "A negative test results means that you are unlikely to have COVID-19."
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "What is the difference between an Antigen test and PCR test?",
                answer = "PCR (polymerase chain reaction) tests have been designed to find genetic material called RNA that tells the virus to make these proteins. This test the most accurate Covid-19 test. When swabs are taken for PCR tests, reagents are used to convert the RNA into DNA in order to identify any infection within the patient. Antigen tests have been designed to find proteins on the surface of the virus to identify the pathogen. Antigen tests are often called rapid tests and are carried out by mixing the sample with a solution where it releases the viral proteins. The combination is normally applied on a paper strip that will be able to detect any antibodies that bind the protein."
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "How accurate are the antigen tests?",
                answer = "The test meets performance standards of ≥97% specificity, ≥80% sensitivity at viral loads above 100,000 copies/ml"
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "Can I reuse a test?",
                answer = "No. Each test is single use only."
            )
        )

        faqAskList.add(
            FaqAsk(
                question = "Are your kits on the EUR approval list?",
                answer = "Yes."
            )
        )

        faqAskList.add(
            FaqAsk(
                question = "Where can I find more info?",
                answer = "Randox.com"
            )
        )


        return faqAskList
    }

    override fun scrollTo(position: Int) {
        binding.rvFaq.smoothScrollToPosition(position)
    }

    private fun launchYoutube() {
        val appIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${Constants.VIDEO_ID_HOW_IT_WORKS}"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=${Constants.VIDEO_ID_HOW_IT_WORKS}")
        )
        try {
            requireContext().startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            requireContext().startActivity(webIntent)
        }
    }

}