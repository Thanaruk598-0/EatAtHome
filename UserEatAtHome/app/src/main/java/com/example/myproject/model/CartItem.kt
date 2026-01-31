package com.example.myproject.model

data class CartItem(
    val foodName: String? = null,
    var foodPrice: String? = null,
    val foodDescription: String? = null,
    val foodImage: String? = null,
    var foodQuantity: Int = 1
)
