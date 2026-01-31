package com.example.myproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myproject.databinding.ActivityDetailsBinding
import com.example.myproject.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var auth: FirebaseAuth

    private var foodName: String = "No Name"
    private var foodPrice: String = "0"
    private var foodDescription: String = "No Description"
    private var foodImage: String = ""
    private var foodIngredients: String = "No Ingredients"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // รับค่าจาก Intent
        foodName = intent.getStringExtra("MenuItemName") ?: "No Name"
        foodPrice = intent.getStringExtra("MenuItemPrice") ?: "0"
        foodDescription = intent.getStringExtra("MenuItemDescription") ?: "No Description"
        foodImage = intent.getStringExtra("MenuItemImage") ?: ""
        foodIngredients = intent.getStringExtra("MenuItemIngredients") ?: "No Ingredients"

        Log.d("DetailsActivity", "Received foodName: $foodName, Price: $foodPrice")

        binding.detailFoodName.text = foodName
        binding.detailDescription.text = foodDescription
        binding.detailIngredients.text = foodIngredients

        binding.imageButton.setOnClickListener {
            finish()
        }

        binding.addItemButton.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val user = auth.currentUser

        if (user != null) {
            val emailKey = user.email?.replace(".", "_")?.replace("@", "_") ?: ""

            // ตรวจสอบ `foodPrice` ก่อนเพิ่มลง Firebase
            if (foodPrice == "0" || foodPrice.isEmpty()) {
                database.child("menu").orderByChild("foodName").equalTo(foodName)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (menuItem in snapshot.children) {
                                val realPrice = menuItem.child("foodPrice").getValue(String::class.java)
                                if (!realPrice.isNullOrEmpty()) {
                                    foodPrice = realPrice
                                }
                            }
                            addCartItemToDatabase(emailKey)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("DetailsActivity", "Failed to get food price: ${error.message}")
                        }
                    })
            } else {
                addCartItemToDatabase(emailKey)
            }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addCartItemToDatabase(emailKey: String) {
        val database = FirebaseDatabase.getInstance().reference
        val cartItem = CartItem(
            foodName = foodName,
            foodPrice = foodPrice,
            foodDescription = foodDescription,
            foodImage = foodImage,
            foodQuantity = 1
        )

        database.child("Users").child(emailKey).child("CartItems").push()
            .setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Added To Cart Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Add To Cart", Toast.LENGTH_SHORT).show()
                Log.e("DetailsActivity", "Error adding to cart: ${it.message}")
            }
    }

}
