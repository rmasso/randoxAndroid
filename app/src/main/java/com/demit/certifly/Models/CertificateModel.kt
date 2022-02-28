package com.demit.certifly.Models

data class CertificateModel(
    var token:String ="",
    var usr_id: String = "",
    var cert_name: String = "",
    var cert_email: String = "",
    var cert_passport: String = "",
    var cert_country: String = "",
    var cert_device_id: String = "",
    var cert_ai_pred: String = "",
    var cert_ai_approved: String = "",
    var cert_manual_approved:String="N",
    var cert_create: String = "",
    var cert_deviceToken: String= "",
    var cert_image: String = "",
    var cert_nationality: String="",
    var is_viccinated: String="",
    var vaccine_name:String="",
    var is_fully_vaccinated_14days_uk:String="",
    var pfl_code:String="",
    var two_day_booking_ref:String="",
    var transport_type:String="",
    var isolation_address_line1:String="",
    var isolation_address_line2:String="",
    var town:String="",
    var post_code:String="",
    var arrival_date:String="",
    var fligh_vessel_train_no:String="",
    var nhs_no:String="",
    var country_territory_part_journey:String="",
    var last_date_department:String="",
    var Country_of_departure:String="",
    val companyName:String="RandoxNew",
    var swap_timestamp:String="",
    var departure_date:String=""




)