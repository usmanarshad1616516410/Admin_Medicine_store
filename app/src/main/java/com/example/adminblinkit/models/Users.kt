package com.example.adminblinkit.models

import com.example.adminblinkit.utils.Utils

data class Users(
    var uid : String? =null,
    val adminName : String?=null,
    val adminPhoneNumber : String? =null,
    val adminEmail : String?=null,
    val adminAddress : String? =null,
    val adminPassword : String?=null,
    var adminToken : String?=null,
)
