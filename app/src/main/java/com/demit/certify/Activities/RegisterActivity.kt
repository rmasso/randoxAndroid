package com.demit.certify.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.demit.certify.R
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.BlinkIdUISettings
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    //Blink Id Variables
    private lateinit var mRecognizer: BlinkIdCombinedRecognizer
    private lateinit var mRecognizerBundle: RecognizerBundle
    private val MY_REQUEST_CODE = 801
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mRecognizer = BlinkIdCombinedRecognizer()
        mRecognizer.setReturnFaceImage(true)
        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = RecognizerBundle(mRecognizer)
        startScanning()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === MY_REQUEST_CODE) {
            if (resultCode === RESULT_OK && data != null) {
                // load the data into all recognizers bundled within your RecognizerBundle
                mRecognizerBundle.loadFromIntent(data)

                // now every recognizer object that was bundled within RecognizerBundle
                // has been updated with results obtained during scanning session
                // you can get the result by invoking getResult on recognizer
                val result = mRecognizer.result
                // result is valid, you can use it however you wish
                if (result.resultState == Recognizer.Result.State.Valid) {
                    fname.text= Editable.Factory.getInstance().newEditable( result.firstName)
                    sname.text= Editable.Factory.getInstance().newEditable( result.lastName)


                    result.dateOfBirth.date?.let {date->
                        val day= date.day
                        val year= date.year
                        val month= date.month
                        dob.text= Editable.Factory.getInstance().newEditable("$day/$month/$year")
                    }


                    pnumber.text= Editable.Factory.getInstance().newEditable( result.documentNumber)



                    /* val imageArr = result.encodedFaceImage
                     faceImage.setImageBitmap(
                         result.faceImage?.convertToBitmap()
                     )*/

                    /*     val p = ProfileModel();
                     list.add(p)
                     binding.rv.getAdapter()?.itemCount?.minus(1)?.let { it1 ->
                         binding.rv.smoothScrollToPosition(
                             it1
                         )
                     };
                     setData(list.size - 1)*/

                }
            }
        }
    }

    private fun startScanning() {
        // Settings for BlinkIdActivity
        val settings = BlinkIdUISettings(mRecognizerBundle)

        // tweak settings as you wish

        // Start activity
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, settings)
    }
}