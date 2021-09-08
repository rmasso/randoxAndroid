package com.demit.certify.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import com.demit.certify.Activities.DeviceScanActivity
import com.demit.certify.R
import com.demit.certify.databinding.FragmentScancompleteBinding
import java.io.File


class ScancompleteFragment : Fragment() {
    val SCAN_RESULT=150
    lateinit var binding: FragmentScancompleteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScancompleteBinding.inflate(layoutInflater)
        setclicks()
        startScanning()
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==SCAN_RESULT){
            if(resultCode!= Activity.RESULT_CANCELED){
                //Todo add image initilization related code here
                data?.let {
                    val originalImageFile= it.getSerializableExtra("original_image") as File
                    val croppedImageFile= it.getSerializableExtra("cropped_image") as File
                    binding.scannedImage.setImageBitmap(BitmapFactory.decodeFile(croppedImageFile.path))
                }

            }
        }

    }
    fun setclicks(){
        binding.submit.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragcontainer,ScansubmitFragment())?.addToBackStack("")?.commit()
        }
        binding.rescan.setOnClickListener {
            startScanning()
        }
    }

    private fun startScanning(){
//        val intent= Intent(requireContext(), DeviceScanActivity::class.java)
//        startActivityForResult(intent,SCAN_RESULT)
    }
}