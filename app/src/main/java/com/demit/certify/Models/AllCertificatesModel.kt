package com.demit.certify.Models

data class AllCertificatesModel (
    val cert_name : String,
    val cert_passport : String,
    val cert_country : String,
    val cert_device_id : String,
    val cert_ai_pred : String,
    val cert_ai_approved : String,
    val cert_manual_approved : String,
    val cert_create : String,
    val cert_email : String,
    val cert_image : String,
    val cert_uuid : String
)