package com.demit.certify.Models

import java.io.File

data class CertificateModel(
    val usr_id: String = "",
    val cert_name: String = "",
    val cert_email: String = "",
    val cert_passport: String = "",
    val cert_country: String = "",
    val cert_device_id: String = "",
    val cert_ai_pred: String = "",
    val cert_ai_approved: String = "",
    val cert_create: String = "",
    val cert_deviceToken: String= "",
    val cert_image: String = ""
)