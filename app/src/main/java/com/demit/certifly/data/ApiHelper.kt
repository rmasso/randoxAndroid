package com.demit.certifly.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.demit.certifly.Extras.Constants
import com.demit.certifly.Models.CertificateModel
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.Models.TypeStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

object ApiHelper {

    private object ApiEndPoint {
        val CREATE_NEW_CERTIFICATE = "setCert.php"
        val VALIDATE_QRCODE = "getQrVerification.php"
        val FAMILY_REGISTER = "FamilyRegistration.php"
        val PLF_VERIFICATION = "getPLFVerificationRandox.php"
        val FORGET_PASSWORD = "forgot_password.php"
        val PLF_FIT_VERIFICATION = "verify_pfl_fit.php"
        val TEST_TYPE_AVAILABILITY = "getTypeStatus.php"
        val MODIFY_FAMILY_USER="modifyFamily.php"
        val MODIFY_MAIN_USER="modifyUser.php"
    }

    fun createNewCertificate(certificateModel: CertificateModel): LiveData<String> {
        val apiResponseLiveData = MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.CREATE_NEW_CERTIFICATE}")
            .addBodyParameter(certificateModel)
            .setTag("test")
            .setPriority(Priority.HIGH)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    apiResponseLiveData.postValue("$response")
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++", "${anError?.message}")
                }
            })

        return apiResponseLiveData
    }

    fun isQrValid(token: String, deviceQr: String): LiveData<String> {
        val apiResponseLiveData = MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.VALIDATE_QRCODE}")
            .addBodyParameter("token", token)
            .addBodyParameter("device_qr", deviceQr)
            .addBodyParameter("companyName", "Randox")
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    apiResponseLiveData.postValue(response?.getString("ret"))
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++", "${anError?.message}")
                }
            })

        return apiResponseLiveData
    }

    fun postFamilyUser(profileMap: Map<String, String>): LiveData<String> {
        val apiResponseLiveData = MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.FAMILY_REGISTER}")
            .addBodyParameter(profileMap)
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    apiResponseLiveData.postValue(response?.getString("ret"))
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++", "${anError?.message}")
                }
            })

        return apiResponseLiveData
    }

    fun updateFamilyUser(profileMap:Map<String,String>,isMainUser:Boolean):LiveData<String>{
        val apiResponseLiveData = MutableLiveData<String>()
        val endPoint= if(isMainUser) ApiEndPoint.MODIFY_MAIN_USER else ApiEndPoint.MODIFY_FAMILY_USER

        AndroidNetworking.post("${Constants.url}$endPoint")
            .addBodyParameter(profileMap)
            .setTag("update_user")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    apiResponseLiveData.postValue(response?.getString("ret"))
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++", "${anError?.message}")
                }
            })


        return apiResponseLiveData
    }

    fun verifyPlfNumber(token: String, plfNum: String): LiveData<String> {
        val apiResponseLiveData = MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.PLF_VERIFICATION}")
            .addBodyParameter("token", token)
            .addBodyParameter("cert_pfl", plfNum)
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    apiResponseLiveData.postValue(response?.getString("ret"))
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++", "${anError?.message}")
                }
            })

        return apiResponseLiveData
    }

    fun verifyPlfFitNumber(token: String, plfNum: String): LiveData<String> {
        val apiResponseLiveData = MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.PLF_FIT_VERIFICATION}")
            .addBodyParameter("token", token)
            .addBodyParameter("cert_pfl", plfNum)
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    apiResponseLiveData.postValue(response?.getString("ret"))
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++", "${anError?.message}")
                }
            })

        return apiResponseLiveData
    }

    fun forgetPassword(token: String, email: String): LiveData<String> {
        val apiResponseLiveData = MutableLiveData<String>()

        AndroidNetworking.post("${Constants.url}${ApiEndPoint.FORGET_PASSWORD}")
            .addBodyParameter("token", token)
            .addBodyParameter("email", email)
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    apiResponseLiveData.postValue(response?.getString("ret"))
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue("Something went wrong")
                    Log.d("++err++", "${anError?.message}")
                }
            })

        return apiResponseLiveData
    }

    fun getTestAvailability(): LiveData<List<TypeStatus>?> {
        val apiResponseLiveData = MutableLiveData<List<TypeStatus>?>()
        AndroidNetworking.post("${Constants.url}${ApiEndPoint.TEST_TYPE_AVAILABILITY}")
            .addBodyParameter("companyName", "Randox")
            .setTag("test_availability")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object :JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    val gson= Gson()
                    val listType = object : TypeToken<List<TypeStatus>?>() {}.type
                    val statusTypeList: MutableList<TypeStatus> =
                        gson.fromJson(response?.get("ret").toString(), listType)
                    apiResponseLiveData.postValue(statusTypeList)
                }

                override fun onError(anError: ANError?) {
                    apiResponseLiveData.postValue(null)
                }

            })

        return apiResponseLiveData
    }

}