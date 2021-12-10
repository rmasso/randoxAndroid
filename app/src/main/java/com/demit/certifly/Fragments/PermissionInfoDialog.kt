package com.demit.certifly.Fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.demit.certifly.Extras.Constants.Companion.DIALOG_TYPE_CAMERA_DEVICE_SCAN
import com.demit.certifly.Extras.Constants.Companion.DIALOG_TYPE_CAMERA_PASSPORT
import com.demit.certifly.Extras.Constants.Companion.DIALOG_TYPE_STORAGE
import com.demit.certifly.R
import com.demit.certifly.databinding.DialogPermissionInfoBinding

class PermissionInfoDialog(
    val shouldShowSettingsContent: Boolean,
    val dialogType: Int,
    val onButtonClick: (id: Int,dialog: PermissionInfoDialog) -> Unit
) : DialogFragment(),View.OnClickListener {
    lateinit var binding: DialogPermissionInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_permission_info,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       initContent()
       attachClickListeners()

    }

    override fun onStart() {
        super.onStart()
        /*val width = resources.displayMetrics.widthPixels * 0.90
        val height = resources.displayMetrics.heightPixels * 0.90
        dialog!!.window!!.setLayout(width.toInt(), height.toInt())*/
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onClick(v: View) {
        onButtonClick(v.id,this)
    }

    private fun initContent(){
        with(binding) {
            when (dialogType) {
                DIALOG_TYPE_CAMERA_PASSPORT -> {
                    headerText.text = getString(R.string.camera_permission_header)
                    body.text =
                        if (shouldShowSettingsContent) getString(R.string.camera_permission_content_passport_settings)
                        else getString(R.string.camera_permission_content_passport)

                }
                DIALOG_TYPE_CAMERA_DEVICE_SCAN -> {
                    headerText.text = getString(R.string.camera_permission_header)
                    body.text =
                        if (shouldShowSettingsContent) getString(R.string.camera_permission_content_device_settings)
                        else getString(R.string.camera_permission_content_device_scan)

                }
                DIALOG_TYPE_STORAGE -> {
                    headerText.text = getString(R.string.storage_permission_header)
                    body.text =
                        if (shouldShowSettingsContent) getString(R.string.storage_permission_content_settings)
                        else getString(R.string.storage_permission_content)

                }
            }
            if (shouldShowSettingsContent) {
                btnAllow.visibility = View.GONE
                btnSettings.visibility = View.VISIBLE
            }

        }
    }
    private fun attachClickListeners() {
        with(binding){
            if(shouldShowSettingsContent)
                btnSettings.setOnClickListener(this@PermissionInfoDialog)
            else
                btnAllow.setOnClickListener(this@PermissionInfoDialog)
            btnNotNow.setOnClickListener(this@PermissionInfoDialog)
        }
    }
}