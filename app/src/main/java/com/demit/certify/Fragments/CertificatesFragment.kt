package com.demit.certify.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.demit.certify.R
import com.demit.certify.databinding.FragmentCertificatesBinding
import java.util.ArrayList

class CertificatesFragment : Fragment() {

    lateinit var binding: FragmentCertificatesBinding
    lateinit var adapter : CertificateAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCertificatesBinding.inflate(layoutInflater)
        adapter = CertificateAdapter(requireContext(), 0)
        binding.stackview.adapter = adapter
        return binding.root
    }

    inner class CertificateAdapter(context: Context, resource: Int) : ArrayAdapter<Any?>(context, resource) {
        var l: MutableList<Int> = ArrayList()
        override fun getCount(): Int {
            return l.size
        }

        override fun getItem(position: Int): Any? {
            return l[position]
        }

        override fun getView(position: Int, convertview: View?, parent: ViewGroup): View {
            var convertView = convertview
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_certificates, parent, false)
            }
            val n = l[position].toString()

            Log.d("aaa", n)

            return convertView!!
        }

        init {
            l.add(1)
            l.add(2)
            l.add(3)
            l.add(4)
            l.add(5)
            l.add(6)
            l.add(7)
            l.add(8)
            l.add(9)
            l.add(10)
            l.add(11)
            l.add(12)
            l.add(13)
            l.add(14)
            l.add(15)
            l.add(16)
            l.add(17)
            l.add(18)
            l.add(19)
            l.add(20)
        }
    }
}