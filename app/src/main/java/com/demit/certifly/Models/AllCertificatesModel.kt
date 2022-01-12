package com.demit.certifly.Models

import java.io.Serializable



data class AllCertificatesModel(
    val cert_name: String,
    val cert_id:String,
    val usr_id:String,
    val usr_birth:String,
    val usr_phone:String,
    val cert_sex:String,
    val cert_usr_id:String,
    val cert_usr_parent_id:String,
    val cert_email: String,
    val cert_passport: String,
    val cert_country: String,
    val cert_device_id: String,
    val cert_ai_pred: String,
    val cert_ai_approved: String,
    val cert_manual_approved: String,
    val cert_create: String,
    val cert_timestamp:String,
    val cert_proc_stop:String,
    val cert_deviceToken:String,
    val cert_uuid: String,
    val is_viccinated: String,
    val pfl_code:String
) : Serializable