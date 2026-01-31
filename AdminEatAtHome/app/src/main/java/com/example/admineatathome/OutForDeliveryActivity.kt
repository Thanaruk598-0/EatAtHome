package com.example.admineatathome

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admineatathome.adapter.DeliveryAdapter
import com.example.admineatathome.databinding.ActivityOutForDeliveryBinding
import com.example.admineatathome.model.OrderDetails
import com.google.firebase.database.*

class OutForDeliveryActivity : AppCompatActivity() {

    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: DeliveryAdapter
    private var listOfOrders: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        setupRecyclerView()
        loadOutForDeliveryOrders()

        binding.backbtn.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = DeliveryAdapter(listOfOrders, this) { order ->
            updateOrderStatus(order) // ✅ เรียกใช้งาน updateOrderStatus
        }
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.deliveryRecyclerView.adapter = adapter
    }

    private fun loadOutForDeliveryOrders() {
        val deliveryRef = database.reference.child("OutForDelivery")

        deliveryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrders.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val order = data.getValue(OrderDetails::class.java)
                        order?.let { listOfOrders.add(it) }
                    }
                    adapter.notifyDataSetChanged() // ✅ อัปเดต RecyclerView
                } else {
                    Log.d("FirebaseData", "No orders in OutForDelivery")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OutForDeliveryActivity, "Failed to load orders", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseData", "Error: ${error.message}")
            }
        })
    }

    private fun updateOrderStatus(order: OrderDetails) {
        val databaseRef = database.reference.child("OutForDelivery").child(order.currentTime.toString())

        databaseRef.child("orderStatus").setValue(mutableListOf("Delivered")) // ✅ เปลี่ยนค่าเป็น MutableList
            .addOnSuccessListener {
                Toast.makeText(this, "Order marked as Delivered", Toast.LENGTH_SHORT).show()

                // ✅ อัปเดต UI ให้แสดง "Delivered"
                val index = listOfOrders.indexOfFirst { it.currentTime == order.currentTime }
                if (index != -1) {
                    listOfOrders[index].orderStatus = mutableListOf("Delivered")
                    adapter.notifyItemChanged(index) // ✅ อัปเดต UI เฉพาะรายการที่เปลี่ยน
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
    }
}
