package com.example.admineatathome

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admineatathome.adapter.OrderDetailsAdapter
import com.example.admineatathome.databinding.ActivityOrderDetailsBinding
import com.example.admineatathome.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backeButton.setOnClickListener {
            finish()
        }

        // ✅ รับข้อมูลจาก Intent และอัปเดต UI
        val orderDetails: OrderDetails? = intent.getParcelableExtra("orderDetails")

        orderDetails?.let {
            binding.name.text = it.userName ?: "N/A"
            binding.address.text = it.address ?: "N/A"
            binding.phone.text = it.phoneNumber ?: "N/A"
            binding.totalAmount.text = "${it.totalPrice} ฿"

            Log.d("OrderDetails", "FoodNames: ${it.foodNames}")
            Log.d("OrderDetails", "FoodPrices: ${it.foodPrices}")
            Log.d("OrderDetails", "FoodQuantities: ${it.foodQuantities}")

            // ✅ เช็คว่ามีข้อมูลอาหารหรือไม่
            if (!it.foodNames.isNullOrEmpty() && !it.foodPrices.isNullOrEmpty() && !it.foodQuantities.isNullOrEmpty()) {
                setupRecyclerView(it.foodNames!!, it.foodPrices!!, it.foodQuantities!!)
            } else {
                Log.e("OrderDetails", "No food items found!")
            }
        }
    }

    private fun setupRecyclerView(foodNames: List<String>, foodPrices: List<String>, foodQuantities: List<Int>) {
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewOrders.adapter = OrderDetailsAdapter(foodNames, foodPrices, foodQuantities)
    }
}
