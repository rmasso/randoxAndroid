package com.demit.certifly.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.demit.certifly.R
import com.demit.certifly.databinding.DialogPermissionBinding
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.method.LinkMovementMethod
import kotlinx.android.synthetic.main.permission_detail.view.*
import kotlinx.android.synthetic.main.permission_heading.view.*


class PermissionDialog(val onButtonClick: (id: Int) -> Unit) : DialogFragment(),
    View.OnClickListener {
    lateinit var binding: DialogPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_permission,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initPermissionData()
        attachClicks()

    }

    override fun onStart() {
        super.onStart()
        val width = resources.displayMetrics.widthPixels * 0.90
        dialog!!.window!!.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_agree -> {
                onButtonClick(R.id.btn_agree)
                dismiss()
            }
        }
    }

    private fun initPermissionData(){
        binding.link.movementMethod= LinkMovementMethod.getInstance()
        with(binding.contentContainer){
            cameraExpandable.parentLayout.data_heading.text= getString(R.string.camera)
            cameraExpandable.parentLayout.data_heading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera,0,0,0)
            cameraExpandable.secondLayout.data_content.text= getText(R.string.camera_permission)

            storageExpandable.parentLayout.data_heading.text= getString(R.string.storage)
            storageExpandable.parentLayout.data_heading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_storage,0,0,0)
            storageExpandable.secondLayout.data_content.text= getText(R.string.storage_permission)
            }


    }

    private fun attachClicks() {
        binding.btnAgree.setOnClickListener(this)
        with(binding.contentContainer){

            //Parent layout click case
            cameraExpandable.parentLayout.setOnClickListener {
                if (!cameraExpandable.isExpanded)
                    cameraExpandable.expand()
                else
                    cameraExpandable.collapse()
            }

            storageExpandable.parentLayout.setOnClickListener {
                if (!storageExpandable.isExpanded)
                    storageExpandable.expand()
                else
                    storageExpandable.collapse()
            }
        }

    }
}