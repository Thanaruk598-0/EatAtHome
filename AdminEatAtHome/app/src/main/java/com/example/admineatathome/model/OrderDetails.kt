package com.example.admineatathome.model

import android.os.Parcel
import android.os.Parcelable

data class OrderDetails(
    var userUid: String? = null,
    var userName: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
    var totalPrice: String? = null,
    var foodNames: MutableList<String> = mutableListOf(),
    var foodPrices: MutableList<String> = mutableListOf(),
    var foodQuantities: MutableList<Int> = mutableListOf(),
    var foodImages: MutableList<String> = mutableListOf(),
    var orderStatus: MutableList<String> = mutableListOf("Pending"),
    var currentTime: Long = System.currentTimeMillis()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        userUid = parcel.readString(),
        userName = parcel.readString(),
        address = parcel.readString(),
        phoneNumber = parcel.readString(),
        totalPrice = parcel.readString(),
        foodNames = parcel.createStringArrayList() ?: mutableListOf(),
        foodPrices = parcel.createStringArrayList() ?: mutableListOf(),
        foodQuantities = (parcel.readArrayList(Int::class.java.classLoader) as? MutableList<Int>) ?: mutableListOf(),
        foodImages = parcel.createStringArrayList() ?: mutableListOf(),
        orderStatus = parcel.createStringArrayList() ?: mutableListOf("Pending"),
        currentTime = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(phoneNumber)
        parcel.writeString(totalPrice)
        parcel.writeStringList(foodNames)
        parcel.writeStringList(foodPrices)
        parcel.writeList(foodQuantities)
        parcel.writeStringList(foodImages)
        parcel.writeStringList(orderStatus)
        parcel.writeLong(currentTime)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails = OrderDetails(parcel)
        override fun newArray(size: Int): Array<OrderDetails?> = arrayOfNulls(size)
    }
}
