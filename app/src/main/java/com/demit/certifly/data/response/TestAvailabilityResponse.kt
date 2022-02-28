package com.demit.certifly.data.response

import com.demit.certifly.Models.TypeStatus

data class TestAvailabilityResponse(val testTypeStatusList:List<TypeStatus>?, val message:String="")
