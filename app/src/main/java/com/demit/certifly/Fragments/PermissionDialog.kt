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
        binding.agreeBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            binding.contentContainer.storageMessageContainer.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        val width = resources.displayMetrics.widthPixels * 0.80
        val height = resources.displayMetrics.heightPixels * 0.80
        dialog!!.window!!.setLayout(width.toInt(), height.toInt())
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancelBtn -> {
                onButtonClick(R.id.cancelBtn)
                dismiss()
            }
            R.id.agreeBtn -> {
                onButtonClick(R.id.agreeBtn)
                dismiss()
            }
        }
    }
}