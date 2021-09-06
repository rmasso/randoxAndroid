package com.demit.certify.Activities

import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import com.demit.certify.Extras.CameraPreview
import com.demit.certify.R
import com.demit.certify.databinding.ActivityScanBinding
import java.lang.Exception

class ScanActivity : AppCompatActivity() {
    lateinit var binding : ActivityScanBinding
    lateinit var camera : Camera
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    fun init(){
        camera = cameraInstance!!
        val cameraPreview = CameraPreview(this, camera)
        val cameraview = binding.camera
        cameraview?.addView(cameraPreview)
    }
    private val cameraInstance: Camera?
        get() {
            var c: Camera? = null
            try {
                c = Camera.open()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return c
        }

    override fun onPause() {
        camera.stopPreview()
        super.onPause()
    }

    override fun onResume() {
        camera.startPreview()
        super.onResume()
    }

    override fun onStop() {
        camera.stopPreview()
        super.onStop()
    }

    override fun onDestroy() {
        camera.stopPreview()
        super.onDestroy()
    }
}