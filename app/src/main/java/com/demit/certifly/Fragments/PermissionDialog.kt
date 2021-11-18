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
        binding.btnAgree.setOnClickListener(this)
        binding.link.movementMethod= LinkMovementMethod.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val width = resources.displayMetrics.widthPixels * 0.90
        val height = resources.displayMetrics.heightPixels * 0.90
        dialog!!.window!!.setLayout(width.toInt(), height.toInt())
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
}