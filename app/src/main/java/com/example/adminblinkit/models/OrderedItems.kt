package com.example.adminblinkit.models

data class OrderedItems(
    val orderId : String? = null,
    val itemDate : String? = null,
    val itemStatus : Int? = null,
    val itemTitle : String? = null,
    val itemPrice : Int? = null,
    val userAddress : String? = null,
    val orderingUserUid : String? = null,
    val adminUid : String? = null,

)
