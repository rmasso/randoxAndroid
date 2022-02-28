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
import com.demit.certifly.Models.AllCertificatesModel
import com.demit.certifly.Models.CertificateModel
import com.demit.certifly.Models.TProfileModel
import com.demit.certifly.Models.TypeStatus
import com.demit.certifly.data.response.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

object ApiHelper {

    private object ApiEndPoint {


        //New Api endpoints
        const val LOGIN="login"
        const val SIGNUP="signup"
        const val FORGOT_PASSWORD="forgotPassword"
        const val FAMILY_SIGNUP = "family_signup"
        const val Fetch_CERTIFICATES = "getAllCert"
        const val DELETE_CERTIFICATE = "cert_soft_del"
        const val Fetch_USER_PROFILES = "userprofile"
        const val TEST_TYPE_AVAILABILITY = "getCertType"
        const val BOOK_REFERENCE_VERIFICATION_PAGE = "plf_verification"
        const val PURCHASE_ORDER_VERIFICATION_PAGE = "verify_plf_fit"
        const val QR_VERIFICATION = "qrVerification"
        const val CREATE_CERTIFICATE = "setCert"
        const val UPDATE_MAIN_USER = "update_user_profile"
        const val UPDATE_FAMILY_USER = "update_family_user"

        // const val Lab_PAGE = "pcr_lab_list"
        // const val BOOK_PCR_PAGE = "pcr_lab_test"
        // const val GET_PCR_RESERVATION_PAGE = "getReservationList"
    }

    //New Api's
    fun login(email: String, password: String): LiveData<LoginResponse> {
        val apiResponseLiveData = MutableLiveData<LoginResponse>()

        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.LOGIN}")
            .addBodyParameter("email", email)
            .addBodyParameter("password", password)
            .setTag("login")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {

                        if (it.getString("status") == "200") {
                            apiResponseLiveData.value =
                                LoginResponse("", it.getString("accessToken"))
                        }
                    }

                }

                override fun onError(anError: ANError?) {

                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value = LoginResponse(it.message, "")
                        } ?: run {
                            apiResponseLiveData.value = LoginResponse("Something went wrong", "")
                        }

                    } else

                        apiResponseLiveData.value =
                            LoginResponse("Check your internet connection", "")


                }

            })

        return apiResponseLiveData
    }

    fun signup(userMap: Map<String, String>): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()

        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.SIGNUP}")
            .addBodyParameter(userMap)
            .setTag("signup")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        if (it.getString("status") == "201") {
                            apiResponseLiveData.value =
                                ApiResponse(true, "User Registered successfully")
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }

                }

                override fun onError(anError: ANError?) {

                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value = ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")


                }

            })

        return apiResponseLiveData


    }

    fun createFamilyUser(token: String, profileMap: Map<String, String>): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()

        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.FAMILY_SIGNUP}")
            .addHeaders("Authorization", "Bearer $token")
            .addBodyParameter(profileMap)
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        if (it.getString("status") == "201") {
                            apiResponseLiveData.value =
                                ApiResponse(true, it.getString("message"))
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }

                }

                override fun onError(anError: ANError?) {
                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")

                }
            })

        return apiResponseLiveData
    }

    fun updateUser(token: String, profileMap: Map<String, String>, isMainUser: Boolean): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()
        val endPoint =
            if (isMainUser) ApiEndPoint.UPDATE_MAIN_USER else ApiEndPoint.UPDATE_FAMILY_USER

        AndroidNetworking.post("${Constants.NEW_URL}$endPoint")
            .addHeaders("Authorization", "Bearer $token")
            .addBodyParameter(profileMap)
            .setTag("update_user")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        if (it.getString("status") == "200") {
                            apiResponseLiveData.value =
                                ApiResponse(true, it.getString("message"))
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }

                }

                override fun onError(anError: ANError?) {
                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")

                }
            })


        return apiResponseLiveData
    }

    fun fetchUserProfiles(token: String): LiveData<FetchUserProfileListResponse> {
        val apiResponseLiveData = MutableLiveData<FetchUserProfileListResponse>()

        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.Fetch_USER_PROFILES}")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("fetch users")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        if (it.getString("status") == "200") {

                            val dataObj = it.getJSONObject("data")

                            val mainUser = TProfileModel().apply {
                                usr_id = dataObj.getString("usr_id")
                                usr_firstname = dataObj.getString("usr_firstname")
                                usr_surname = dataObj.getString("usr_surname")
                                usr_admin = dataObj.getString("usr_admin")
                                usr_sex = dataObj.getString("usr_sex")
                                usr_birth = dataObj.getString("usr_birth")
                                usr_home = dataObj.getString("usr_home")
                                usr_zip = dataObj.getString("usr_zip")
                                usr_passport = dataObj.getString("usr_passport")
                                usr_country = dataObj.getString("usr_country")
                                usr_phone = dataObj.getString("usr_phone")
                                email = dataObj.getString("email")
                                ethnicity = dataObj.getString("ethnicity")
                                usr_city = dataObj.getString("usr_city")
                                usr_addressLine2 = dataObj.getString("usr_addressLine2")
                            }

                            val gson = Gson()
                            val objectType =
                                object : TypeToken<List<TProfileModel?>?>() {}.type
                            val familyUsers: MutableList<TProfileModel> =
                                gson.fromJson(dataObj.get("family_user").toString(), objectType)

                            familyUsers.add(0, mainUser)


                            apiResponseLiveData.value = FetchUserProfileListResponse(
                                familyUsers, ""
                            )
                        } else {
                            apiResponseLiveData.value =
                                FetchUserProfileListResponse(null, it.getString("message"))
                        }
                    }

                }

                override fun onError(anError: ANError?) {

                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                FetchUserProfileListResponse(null, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                FetchUserProfileListResponse(null, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            FetchUserProfileListResponse(null, "Check your internet connection")


                }

            })

        return apiResponseLiveData
    }

    fun forgetPassword(email: String): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()

        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.FORGOT_PASSWORD}")
            .addBodyParameter("email", email)
            .setTag("forget_password")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {

                    response?.let {
                        if (it.getString("status") == "200") {
                            apiResponseLiveData.value =
                                ApiResponse(true, it.getString("message"))
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }

                }

                override fun onError(anError: ANError?) {

                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")

                }
            })

        return apiResponseLiveData
    }



    fun deleteCertificate(token: String, certId: String): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()
        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.DELETE_CERTIFICATE}")
            .addHeaders("Authorization", "Bearer $token")
            .addBodyParameter("cert_id", certId)
            .setTag("fetch certificates")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        if (it.getString("status") == "200") {
                            apiResponseLiveData.value =
                                ApiResponse(true, it.getString("message"))
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }
                }

                override fun onError(anError: ANError?) {

                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")


                }


            })


        return apiResponseLiveData
    }

    fun createNewCertificate(token: String, certificateModel: CertificateModel): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()

        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.CREATE_CERTIFICATE}")
            .addHeaders("Authorization", "Bearer $token")
            .addBodyParameter(certificateModel)
            .setTag("test")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        if (it.getString("status") == "201") {
                            apiResponseLiveData.value =
                                ApiResponse(true, it.getString("message"))
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")
                }

            })

        return apiResponseLiveData
    }

    fun fetchCertificates(token: String): LiveData<FetchCertificateResponse> {
        val apiResponseLiveData = MutableLiveData<FetchCertificateResponse>()

        AndroidNetworking.get("${Constants.NEW_URL}${ApiEndPoint.Fetch_CERTIFICATES}")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("fetch certificates")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        if (it.getString("status") == "200") {

                            val gson = Gson()
                            val listType =
                                object : TypeToken<List<AllCertificatesModel?>?>() {}.type
                            val certificates: MutableList<AllCertificatesModel> =
                                gson.fromJson(it.get("data").toString(), listType)

                            apiResponseLiveData.value =
                                FetchCertificateResponse(
                                    it.getInt("count"),
                                    certificates,
                                    it.getString("message")
                                )
                        } else {
                            apiResponseLiveData.value =
                                FetchCertificateResponse(-1, null, it.getString("message"))
                        }
                    }

                }

                override fun onError(anError: ANError?) {

                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                FetchCertificateResponse(-1, null, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                FetchCertificateResponse(-1, null, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            FetchCertificateResponse(-1, null, "Check your internet connection")


                }

            })

        return apiResponseLiveData

    }

    fun getTestAvailability(token: String): LiveData<TestAvailabilityResponse> {
        val apiResponseLiveData = MutableLiveData<TestAvailabilityResponse>()
        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.TEST_TYPE_AVAILABILITY}")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("test_availability")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    response?.let {
                        if (it.getString("status") == "200") {
                            val gson = Gson()
                            val listType = object : TypeToken<List<TypeStatus>?>() {}.type
                            val statusTypeList: MutableList<TypeStatus> =
                                gson.fromJson(response.get("data").toString(), listType)
                            apiResponseLiveData.postValue(
                                TestAvailabilityResponse(
                                    statusTypeList,
                                    ""
                                )
                            )
                        } else {
                            apiResponseLiveData.postValue(
                                TestAvailabilityResponse(
                                    null,
                                    "Something went wrong"
                                )
                            )
                        }

                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                TestAvailabilityResponse(null, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                TestAvailabilityResponse(null, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            TestAvailabilityResponse(null, "Something went wrong")
                }

            })

        return apiResponseLiveData
    }

    fun verifyBookingReferenceNumber(token:String,plfRefNo: String): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()
        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.BOOK_REFERENCE_VERIFICATION_PAGE}")
            .addHeaders("Authorization", "Bearer $token")
            .addBodyParameter("cert_pfl", plfRefNo)
            .setTag("test_availability")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {

                    response?.let {
                        if (it.getString("status") == "200") {
                            apiResponseLiveData.value =
                                ApiResponse(true, it.getString("message"))
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")

                }


            })

        return apiResponseLiveData
    }

    fun verifyPurchaseOrderNumber(token:String,orderNo: String): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()
        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.PURCHASE_ORDER_VERIFICATION_PAGE}")
            .addHeaders("Authorization", "Bearer $token")
            .addBodyParameter("cert_pfl", orderNo)
            .setTag("purchase_order")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {

                    response?.let {
                        if (it.getString("status") == "200") {
                            apiResponseLiveData.value =
                                ApiResponse(true, it.getString("message"))
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")

                }


            })

        return apiResponseLiveData
    }

    fun verifyQrCode(token: String, qrCode: String): LiveData<ApiResponse> {
        val apiResponseLiveData = MutableLiveData<ApiResponse>()

        AndroidNetworking.post("${Constants.NEW_URL}${ApiEndPoint.QR_VERIFICATION}")
            .addHeaders("Authorization", "Bearer $token")
            .addBodyParameter("device_qr", qrCode)
            .setTag("qr")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {

                    response?.let {
                        if (it.getString("status") == "200") {
                            apiResponseLiveData.value =
                                ApiResponse(true, it.getString("message"))
                        } else {
                            apiResponseLiveData.value =
                                ApiResponse(false, it.getString("message"))
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError?.errorCode != 0) {

                        val errorObj = anError?.getErrorAsObject(ApiError::class.java)
                        errorObj?.let {
                            apiResponseLiveData.value =
                                ApiResponse(false, errorObj.message)
                        } ?: run {
                            apiResponseLiveData.value =
                                ApiResponse(false, "Something went wrong")
                        }

                    } else

                        apiResponseLiveData.value =
                            ApiResponse(false, "Check your internet connection")

                }


            })


        return apiResponseLiveData
    }

}