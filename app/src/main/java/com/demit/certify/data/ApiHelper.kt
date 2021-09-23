package com.demit.certify.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.demit.certify.Extras.Constants
import com.demit.certify.Extras.Shared
import com.demit.certify.Models.CertificateModel
import org.json.JSONObject

object ApiHelper {

    private object ApiEndPoint {
        val CREATE_NEW_CERTIFICATE="setCert.php"
    }

    fun createNewCertificate(context: Context, certificateModel: CertificateModel): LiveData<String>
    {
        val apiResponseLiveData= MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.CREATE_NEW_CERTIFICATE}")
            .addBodyParameter(certificateModel)
            .addBodyParameter("token",Shared(context).getString("token"))
            .setTag("test")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object :JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        apiResponseLiveData.postValue("${response.getInt("ret")}")
                    }
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Some Error")
                }

            })

        return apiResponseLiveData
    }


}