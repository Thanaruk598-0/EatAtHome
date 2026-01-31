package com.example.myproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.model.OrderDetails
import com.example.myproject.adapter.RecentOrderAdapter
import com.example.myproject.databinding.ActivityRecentOrderItemsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class recentOrderItems : AppCompatActivity() {
    private lateinit var binding: ActivityRecentOrderItemsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var recentOrderAdapter: RecentOrderAdapter

    private val foodNames = arrayListOf<String>()
    private val foodPrices = arrayListOf<String>()
    private val foodImages = arrayListOf<Int>() // ใช้ภาพตัวอย่าง
    private val foodQuantities = arrayListOf<String>() // ✅ เพิ่มจำนวนอาหาร

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentOrderItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView()
        loadRecentOrders()

        binding.backeButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        recentOrderAdapter = RecentOrderAdapter(foodNames, foodPrices, foodImages, foodQuantities)
        binding.recentOrdersRecyclerView.adapter = recentOrderAdapter
        binding.recentOrdersRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadRecentOrders() {
        val user = auth.currentUser
        if (user != null) {
            val userUid = user.uid

            database.child("orders")
                .orderByChild("userUid")
                .equalTo(userUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        foodNames.clear()
                        foodPrices.clear()
                        foodImages.clear()
                        foodQuantities.clear()

                        if (!snapshot.exists()) {
                            Log.e("recentOrderItems", "No recent orders found for user: $userUid")
                            return
                        }

                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderDetails::class.java)
                            if (order != null) {
                                val foodNamesList = order.getFoodNamesList()
                                val foodPricesList = order.getFoodPricesList()
                                val foodQuantitiesList = order.getFoodQuantitiesList()

                                foodNamesList.forEachIndexed { index, name ->
                                    val price = foodPricesList.getOrNull(index)?.toIntOrNull() ?: 0
                                    val quantity = foodQuantitiesList.getOrNull(index) ?: 1
                                    val totalPrice = price * quantity

                                    Log.d("DEBUG", "Name: $name, Price: $price, Quantity: $quantity, Total: $totalPrice")

                                    foodNames.add(name)
                                    foodPrices.add("$totalPrice THB")
                                    foodQuantities.add(quantity.toString())
                                    foodImages.add(R.drawable.menu6)
                                }
                            }
                        }

                        recentOrderAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("recentOrderItems", "Failed to load orders: ${error.message}")
                    }
                })
        }
    }

}