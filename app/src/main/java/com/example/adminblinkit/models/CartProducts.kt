package com.example.adminblinkit.models

data class CartProducts(


    var itemPushKey: String="random",

    var productId: String? =null,

    var productTitle: String? = null,
    var productQuantity: String? = null,
    var productPrice: String? = null,
    var productCount: Int? = null,
    var productStock: Int? = null,
    var productCategory: String? = null,

    var adminUid: String? = null,
    var userUid: String? = null,
    var productImage: String ? = null,

    var productType: String?=null,
) {
}