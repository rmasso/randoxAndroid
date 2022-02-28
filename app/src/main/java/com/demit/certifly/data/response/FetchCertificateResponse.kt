package com.demit.certifly.data.response

import com.demit.certifly.Models.AllCertificatesModel

data class FetchCertificateResponse(val count:Int,val certificates:List<AllCertificatesModel>?,val message:String="")