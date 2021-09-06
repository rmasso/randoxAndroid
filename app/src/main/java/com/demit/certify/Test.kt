package com.demit.certify

import android.content.Context
import android.hardware.Camera
import android.view.View
import com.demit.certify.Extras.CameraPreview
import android.widget.FrameLayout
import com.demit.certify.R
import java.lang.Exception

class Test {
    private fun initialize(v: View, context: Context) {
        val camera = cameraInstance
        val cameraPreview = CameraPreview(context, camera)
        val cameraview = v.findViewById<View>(R.id.camera) as FrameLayout
        cameraview?.addView(cameraPreview)
    }

    private val cameraInstance: Camera?
        private get() {
            var c: Camera? = null
            try {
                c = Camera.open()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return c
        }
}