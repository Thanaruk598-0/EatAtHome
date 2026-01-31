package com.example.myproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myproject.databinding.ActivityPayOutBinding
import com.example.myproject.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button8.setOnClickListener { finish() }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        loadUserProfile()

        val totalCartPrice = intent.getIntExtra("TOTAL_CART_PRICE", 0)
        binding.totalCart.text = "$totalCartPrice THB"

        binding.PlaceMyorder.setOnClickListener { placeOrder(totalCartPrice) }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        val emailKey = user.email?.replace(".", "_")?.replace("@", "_") ?: ""

        database.child("Users").child(emailKey).child("Profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.nameP.setText(snapshot.child("name").value.toString())
                        binding.addressP.setText(snapshot.child("address").value.toString())
                        binding.phoneP.setText(snapshot.child("phone").value.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PayOutActivity", "Failed to load profile: ${error.message}")
                }
            })
    }

    private fun placeOrder(totalPrice: Int) {
        val user = auth.currentUser ?: return
        val emailKey = user.email?.replace(".", "_")?.replace("@", "_") ?: ""

        database.child("Users").child(emailKey).child("CartItems")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) return

                    val orderDetails = OrderDetails().apply {
                        userUid = user.uid
                        userName = binding.nameP.text.toString()
                        address = binding.addressP.text.toString()
                        phoneNumber = binding.phoneP.text.toString()
                        this.totalPrice = totalPrice.toString()
                        orderStatus = listOf("Pending")
                        currentTime = System.currentTimeMillis()
                    }

                    val foodNamesList = mutableListOf<String>()
                    val foodPricesList = mutableListOf<String>()
                    val foodQuantitiesList = mutableListOf<Int>()

                    for (cartSnapshot in snapshot.children) {
                        foodNamesList.add(cartSnapshot.child("foodName").value.toString())
                        foodPricesList.add(cartSnapshot.child("foodPrice").value.toString())
                        foodQuantitiesList.add(cartSnapshot.child("foodQuantity").value.toString().toIntOrNull() ?: 1)
                    }

                    orderDetails.foodNames = foodNamesList
                    orderDetails.foodPrices = foodPricesList
                    orderDetails.foodQuantities = foodQuantitiesList

                    val orderRef = database.child("orders").push()
                    orderRef.setValue(orderDetails)

                    database.child("Users").child(emailKey).child("CartItems").removeValue()

                    val intent = Intent(this@PayOutActivity, MainActivity::class.java)
                    intent.putExtra("SHOW_HISTORY", true)
                    startActivity(intent)
                    finish()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PayOutActivity", "Failed to place order: ${error.message}")
                }
            })
    }
}
