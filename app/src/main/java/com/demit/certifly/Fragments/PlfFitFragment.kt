package com.demit.certifly.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demit.certifly.Extras.Shared
import com.demit.certifly.Extras.Sweet
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.R
import com.demit.certifly.data.ApiHelper
import com.demit.certifly.databinding.FragmentPlfFitBinding


class PlfFitFragment(val selectedProfile: TProfileModel) : Fragment() {
    private lateinit var binding: FragmentPlfFitBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_plf_fit,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnScan.setOnClickListener {
            if (binding.purchaseOrder.text.trim().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Purchase order number is missing",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val sweet = Sweet(requireContext())
                sweet.show("Verifying")

                ApiHelper.verifyPurchaseOrderNumber(
                    Shared(requireContext()).getString("token"),
                    binding.purchaseOrder.text.toString()
                ).observe(viewLifecycleOwner, { validityResponse ->
                    sweet.dismiss()
                    if (validityResponse.success) {

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragcontainer,
                                FragmentConductTest(
                                    selectedProfile,
                                    null,
                                    binding.purchaseOrder.text.toString().trim()
                                )
                            )
                            .addToBackStack("")
                            .commit()


                    } else {

                        Toast.makeText(
                            requireContext(),
                            validityResponse.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                })
            }
        }
    }
}