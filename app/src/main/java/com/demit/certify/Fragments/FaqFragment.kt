package com.demit.certify.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.demit.certify.Interfaces.FaqScrollInterface
import com.demit.certify.Models.FaqAsk
import com.demit.certify.adapters.FaqAdapter
import com.demit.certify.databinding.FragmentFaqBinding


class FaqFragment : Fragment(),FaqScrollInterface {
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
        adapter = FaqAdapter(getSampleList(),this)
    }

    private fun getSampleList(): List<FaqAsk> {
        val faqAskList = ArrayList<FaqAsk>()
        faqAskList.add(FaqAsk(question = "Will this hurt?"
            ,answer = "Possible discomfort during swabbing. Possible incorrect test results"))
        faqAskList.add(
            FaqAsk(
                question = "What are the potential benefits of this test?",
                answer = "The nasal swab may cause slight discomfort. To obtain an accurate test result it is important to swab the nostril as instructed in the test procedure. Discomfort may be increased if swab is inserted beyond recommended depth. If sharp pain is experienced, do not swab the nostril any further."
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "What are the potential risks of this test?",
                answer = "The test can determine if you have COVID-19.\nThe results, along with other information, can help your healthcare provider make informed decisions about your care./nYou can help limit the spread of COVID-19 by knowing your infection status with this test."
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "What are the differences between COVID-19 molecular, antigen, and antibody tests?",
                answer = "Possible discomfort during swabbing. Possible incorrect test results"
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "Howaccurateisthe RapidCOVID-19AntigenSelf-Test?",
                answer = "There are three main types of COVID-19 tests available, and there are significant differences between them. Molecular tests (also known as PCR tests) detect the genetic material of the Coronavirus. The Rapid COVID-19 Antigen Self-Test is an antigen test. Antigen tests detect for proteins, which are small pieces, belonging to the Coronavirus. Antibody test detects anti -bodies that the immune system in your body produces in response to previous COVID-19 infection. Antibody tests cannot be used to diagnose active COVID-19 infection."
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "What does it mean if I have a positive result?",
                answer = "A positive test result means that proteins from the virus that causes COVID-19 was found in your swab sample. It is likely that you may be required to self-isolate at home to prevent the spread of COVID-19. Please also observe the relevant rules for spread control and contact your doctor or local health department. In this case, it is recommended to have the result confirmed with an alternative test method such as a PCR test."
            )
        )
        faqAskList.add(
            FaqAsk(
                question = "What does it mean if I have a negative result?",
                answer = "A negative test results means that you are unlikely to have COVID-19. The test did not"
            )
        )


        return faqAskList
    }

    override fun scrollTo(position: Int) {
        binding.rvFaq.smoothScrollToPosition(position)
    }

}