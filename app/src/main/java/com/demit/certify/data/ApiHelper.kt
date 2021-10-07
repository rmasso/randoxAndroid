package com.demit.certify.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Shared
import com.demit.certify.Models.CertificateModel
import com.demit.certify.Models.TProfileModel
import org.json.JSONObject

object ApiHelper {

    private object ApiEndPoint {
        val CREATE_NEW_CERTIFICATE="setCert.php"
        val VALIDATE_QRCODE="getQrVerification.php"
        val FAMILY_REGISTER="FamilyRegistration.php"
    }

    fun createNewCertificate(certificateModel: CertificateModel): LiveData<String>
    {
        val apiResponseLiveData= MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.CREATE_NEW_CERTIFICATE}")
            .addBodyParameter(certificateModel)
            .setTag("test")
            .setPriority(Priority.HIGH)
            .build()
            .getAsString(object:StringRequestListener {
                override fun onResponse(response: String?) {
                    apiResponseLiveData.postValue("$response")
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++","${anError?.message}")
                }
            })

        return apiResponseLiveData
    }

    fun isQrValid(token:String,deviceQr:String):LiveData<String>{
        val apiResponseLiveData= MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.VALIDATE_QRCODE}")
            .addBodyParameter("token",token)
            .addBodyParameter("device_qr",deviceQr)
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object:JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    apiResponseLiveData.postValue(response?.getString("ret"))
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++","${anError?.message}")
                }
            })

        return apiResponseLiveData
    }

    fun postFamilyUser(profileMap:Map<String,String>):LiveData<String>{
        val apiResponseLiveData= MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.FAMILY_REGISTER}")
            .addBodyParameter(profileMap)
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object:JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    apiResponseLiveData.postValue(response?.getString("ret"))
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++","${anError?.message}")
                }
            })

        return apiResponseLiveData
    }


}