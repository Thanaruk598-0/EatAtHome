package com.example.myproject.model

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class OrderDetails(
    var userUid: String? = null,
    var userName: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
    var totalPrice: String? = null,

    @PropertyName("foodNames") var foodNames: @RawValue Any? = null,
    @PropertyName("foodPrices") var foodPrices: @RawValue Any? = null,
    @PropertyName("foodQuantities") var foodQuantities: @RawValue Any? = null,
    @PropertyName("orderStatus") var orderStatus: @RawValue Any? = null,

    var currentTime: Long = System.currentTimeMillis()
) : Parcelable {

    fun getFoodNamesList(): List<String> {
        return when (foodNames) {
            is List<*> -> (foodNames as List<String>)
            is Map<*, *> -> (foodNames as Map<String, String>).values.toList()
            else -> listOf()
        }
    }

    fun getFoodPricesList(): List<String> {
        return when (foodPrices) {
            is List<*> -> (foodPrices as List<String>)
            is Map<*, *> -> (foodPrices as Map<String, String>).values.toList()
            else -> listOf()
        }
    }

    fun getFoodQuantitiesList(): List<Int> {
        return when (foodQuantities) {
            is List<*> -> (foodQuantities as List<Long>).map { it.toInt() }
            is Map<*, *> -> (foodQuantities as Map<String, Long>).values.map { it.toInt() }
            else -> listOf()
        }
    }
}
