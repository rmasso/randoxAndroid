package com.demit.certifly.Activities

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.demit.certifly.Extras.*
import com.demit.certifly.Fragments.PermissionInfoDialog
import com.demit.certifly.R
import com.demit.certifly.databinding.ActivityCaptureBinding
import com.demit.certifly.extensions.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_capture.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min


class CaptureActivity : AppCompatActivity(), View.OnClickListener {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var currentPhotoPath: String
    private lateinit var binding: ActivityCaptureBinding
    val SCAN_RESULT = 150
    lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    //Barcode Fields
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var imageAnalysis: ImageAnalysis
    private var isQrFound = false
    private var qrValue = ""
    lateinit var originalImage: Bitmap
    var croppedDevice: String = ""

    //Camera Permission Variables
    lateinit var cameraRequestLauncher: ActivityResultLauncher<String>
    var enable = false
    private val CAMERA_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_capture)
        enable =
            getSharedPreferences("app", MODE_PRIVATE).getBoolean("should_take_to_settings", false)
        initBottomSheet()
        attachClickListener()
        initBarcode()
        if (PermissionUtil.isMarshMallowOrAbove()) {
            cameraRequestLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        getSharedPreferences("app", MODE_PRIVATE).edit()
                            .putBoolean("should_take_to_settings", false).apply()
                        startCamera()
                    } else {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            if (!shouldShowRequestPermissionRationale(PermissionUtil.CAMERA_PERMISSION)) {
                                enable = true
                                getSharedPreferences("app", MODE_PRIVATE).edit()
                                    .putBoolean("should_take_to_settings", true).apply()
                            }
                            finish()

                        } else {
                            //Android 11 and up
                            enable = true
                            getSharedPreferences("app", MODE_PRIVATE).edit()
                                .putBoolean("should_take_to_settings", true).apply()
                            finish()
                        }

                    }

                }
        }
        requestCameraPermission()
        outputDirectory = createImageFile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (PermissionUtil.hasCameraPermission(this)) {
                getSharedPreferences("app", MODE_PRIVATE).edit()
                    .putBoolean("should_take_to_settings", false).apply()
                startCamera()
            } else {
                finish()
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.camera_capture_button -> {
                    takePhoto()
                }
                R.id.cancel_btn -> {
                    showAlertDialog()
                }

                R.id.close -> {
                    finish()
                }

                R.id.submit_btn -> {
                    if (qrValue == "" || croppedDevice == "") {
                        Toast.makeText(
                            this,
                            "Cannot proceed because device or qr not found",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {

                        val result = Intent()
                        result.putExtra("device", croppedDevice)
                        result.putExtra("qr", qrValue)
                        setResult(SCAN_RESULT, result)
                        finish()

                    }
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::cameraProvider.isInitialized)
            cameraProvider.unbindAll()
    }


    //Method Helpers
    private fun requestCameraPermission() {
        when {
            PermissionUtil.hasCameraPermission(this) -> {
                startCamera()
            }
            enable -> {
                val permissionSettingsDialog =
                    PermissionInfoDialog(
                        true,
                        Constants.DIALOG_TYPE_CAMERA_DEVICE_SCAN
                    ) { btnClickId, dialog ->
                        when (btnClickId) {
                            R.id.btn_settings -> {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                val uri = Uri.fromParts("package", packageName, null);
                                intent.data = uri;
                                startActivityForResult(intent, 1001)
                                dialog.dismiss()
                            }
                            R.id.btn_not_now -> {
                                dialog.dismiss()
                                finish()
                            }
                        }
                    }
                permissionSettingsDialog.isCancelable = false
                permissionSettingsDialog.show(supportFragmentManager, "permission_dialog")


            }
            else -> {
                //Show some cool ui to the user explaining why we use this permission
                val permissionDialog =
                    PermissionInfoDialog(
                        false,
                        Constants.DIALOG_TYPE_CAMERA_DEVICE_SCAN
                    ) { btnClickId, dialog ->
                        when (btnClickId) {
                            R.id.btn_allow -> {
                                cameraRequestLauncher.launch(PermissionUtil.CAMERA_PERMISSION)
                                dialog.dismiss()
                            }
                            R.id.btn_not_now -> {
                                dialog.dismiss()
                                finish()
                            }
                        }
                    }
                permissionDialog.isCancelable = false
                permissionDialog.show(supportFragmentManager, "permission_dialog")

            }
        }
    }

    //Camera Helper
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            val size = getDeviceResolution()
            val preview = Preview.Builder()
                .setTargetResolution(Size(size.x, size.y))
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(size.x, size.y))
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this@CaptureActivity as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
                enableTapToFocus(camera)


            } catch (exc: Exception) {
                Log.e("++cam++", "Use case binding failed", exc)
            }


        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputDirectory).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("++cam++", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                   /* if (Shared(this@CaptureActivity).getString("email").trim()
                            .equals(Constants.TEST_EMAIL, true)
                    ) {
                        val result = Intent()
                        result.putExtra("device", currentPhotoPath)
                        result.putExtra("qr", "")
                        setResult(SCAN_RESULT, result)
                        finish()
                    } else {*/
                        val bitmap = getCapturedImage()
                        showOrHideBottomSheet(true)
                        binding.sheetContainer.scannedImage.setImageBitmap(bitmap)
                        binding.sheetContainer.qrText.text =
                            if (qrValue != "") "Success" else "Unknown: Contact Supplier"
                        with(binding.sheetContainer.animator) {
                            visibility = View.VISIBLE
                            playAnimation()
                        }
                        detectDevice(bitmap)
                  //  }


                }
            })
    }

    private fun enableTapToFocus(camera: Camera) {
        binding.viewFinder.afterMeasured {
            binding.viewFinder.setOnTouchListener { _, event ->
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            binding.viewFinder.width.toFloat(), binding.viewFinder.height.toFloat()
                        )
                        val autoFocusPoint = factory.createPoint(event.x, event.y)
                        animateFocusRing(event.x, event.y)
                        try {
                            camera.cameraControl.startFocusAndMetering(
                                FocusMeteringAction.Builder(
                                    autoFocusPoint,
                                    FocusMeteringAction.FLAG_AF
                                ).apply {
                                    //focus only when the user tap the preview
                                    disableAutoCancel()
                                }.build()
                            )
                        } catch (e: CameraInfoUnavailableException) {
                            Log.d("ERROR", "cannot access camera", e)
                        }
                        true
                    }
                    else -> false // Unhandled event.
                }
            }
        }
    }

    inline fun View.afterMeasured(crossinline block: () -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }

    private fun animateFocusRing(x: Float, y: Float) {

        // Move the focus ring so that its center is at the tap location (x, y)
        val width: Float = binding.focusRing.width.toFloat()
        val height: Float = binding.focusRing.height.toFloat()
        binding.focusRing.x = x - width / 2
        binding.focusRing.y = y - height / 2

        // Show focus ring
        binding.focusRing.visibility = View.VISIBLE
        binding.focusRing.alpha = 1f

        // Animate the focus ring to disappear
        binding.focusRing.animate()
            .setStartDelay(500)
            .setDuration(300)
            .alpha(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animator: Animator) {
                    binding.focusRing.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
    }

    private fun getCapturedImage(): Bitmap {
        // Get the dimensions of the View
        val targetW: Int = binding.sheetContainer.scannedImage.width
        val targetH: Int = binding.sheetContainer.scannedImage.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                bitmap.rotateImage(90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                bitmap.rotateImage(180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                bitmap.rotateImage(270f)
            }
            else -> {
                bitmap
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.sheetContainer.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showOrHideBottomSheet(show: Boolean) {
        bottomSheetBehavior.state = if (show)
            BottomSheetBehavior.STATE_EXPANDED
        else
            BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun attachClickListener() {
        binding.cameraCaptureButton.setOnClickListener(this)
        binding.close.setOnClickListener { finish() }
        with(binding.sheetContainer) {
            cancelBtn.setOnClickListener(this@CaptureActivity)
            submitBtn.setOnClickListener(this@CaptureActivity)

        }

    }

    //Object Detection Part
    private fun detectDevice(bitmap: Bitmap) {


        // Run ODT and display result
        // Note that we run this in the background thread to avoid blocking the app UI because
        // TFLite object detection is a synchronised process.
        lifecycleScope.launch(Dispatchers.Default) { runObjectDetection(bitmap) }
    }

    private fun runObjectDetection(bitmap: Bitmap) {
        // Step 1: Create TFLite's TensorImage object
        val image = TensorImage.fromBitmap(bitmap)

        // Step 2: Initialize the detector object
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(1)
            .setScoreThreshold(0.5f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "device_prediction.tflite",
            options
        )

        // Step 3: Feed given image to the detector
        val results = detector.detect(image)

        // Step 4: Parse the detection result and show it
        val resultToDisplay = results.map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val text = "${category.label}, ${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            DetectionResult(it.boundingBox, text)
        }
        // Draw the detection result on the bitmap and show it.
        val imgWithResult = bitmap.drawDetectionResult(bitmap, resultToDisplay)
        runOnUiThread {
            if (resultToDisplay.isNotEmpty()) {
                croppedDevice = currentPhotoPath
                val cropImage =
                    bitmap.cropImageInsideBoundingBox(bitmap, resultToDisplay[0].boundingBox)
                Glide.with(this@CaptureActivity)
                    .load(cropImage)
                    .into(binding.sheetContainer.scannedImage)

            } else {

                Toast.makeText(this@CaptureActivity, "No Device Detected", Toast.LENGTH_LONG).show()
            }
            with(binding.sheetContainer.animator) {
                visibility = View.GONE
                playAnimation()
            }

            if (!isQrFound)
                binding.sheetContainer.submitBtn.visibility = View.GONE
            else
                binding.sheetContainer.submitBtn.visibility = View.VISIBLE


        }
    }

    //Barcode member functions
    private fun initBarcode() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            )
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)
        val size = getDeviceResolution()
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(size.x, size.y))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        setupQrScannerAnalyzer()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setupQrScannerAnalyzer() {
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val image = imageProxy.image
            if (image != null) {
                if (!isQrFound) {
                    val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
                    barcodeScanner.process(inputImage)
                        .addOnSuccessListener { barcodeList ->
                            if (barcodeList.isNotEmpty()) {
                                if (!isQrFound) {
                                    val barcode = barcodeList[0].rawValue
                                    qrValue = barcode?.let {
                                        isQrFound = true
                                        it
                                    } ?: run { "" }
                                }
                            }
                        }
                        .addOnFailureListener { qrValue = "" }
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                }
            }
        })
    }

    private fun getDeviceResolution(): Point {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val point = Point()
            display?.getRealSize(point)
            point
        } else {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            Point(metrics.widthPixels, metrics.heightPixels)
        }
    }

    private fun showAlertDialog() {

        val positiveClickListener = DetachableClickListener.wrap { dialog, _ ->
            binding.sheetContainer.submitBtn.visibility = View.VISIBLE
            showOrHideBottomSheet(false)
            isQrFound = false
            qrValue = ""
            croppedDevice = ""
        }

        val negativeClickListener = DetachableClickListener.wrap { dialog, _ ->
            finish()
        }


        val dialog = AlertDialog.Builder(this)
            .setMessage("Do you want to rescan the device?")
            .setPositiveButton("Yes", positiveClickListener)
            .setNegativeButton("Not now", negativeClickListener)
            .setCancelable(false)
            .create()

        //avoid memory leaks
        positiveClickListener.clearOnDetach(dialog)

        dialog.show()
    }


}