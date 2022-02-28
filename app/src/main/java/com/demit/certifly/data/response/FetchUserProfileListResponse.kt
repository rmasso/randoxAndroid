package com.demit.certifly.data.response

import com.demit.certifly.Models.TProfileModel

data class FetchUserProfileListResponse(val users:List<TProfileModel>?, val message:String="")
