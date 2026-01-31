package com.example.admineatathome

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admineatathome.adapter.PendingOrderAdapter
import com.example.admineatathome.databinding.ActivityPendingOrderBinding
import com.example.admineatathome.model.OrderDetails
import com.google.firebase.database.*

class PendingOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingOrderBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: PendingOrderAdapter
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        setupRecyclerView()
        loadPendingOrders()  // ✅ โหลดข้อมูลออเดอร์แบบเรียลไทม์

        binding.backbtn.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = PendingOrderAdapter(listOfOrderItem, this) { order, action ->
            when (action) {
                "accept" -> updateOrderStatus(order, "Accepted")
                "dispatch" -> moveToOutForDelivery(order)
            }
        }
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    private fun loadPendingOrders() {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("orders")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItem.clear()

                for (data in snapshot.children) {
                    try {
                        val order = data.getValue(OrderDetails::class.java)

                        // ✅ ตรวจสอบว่าข้อมูลไม่เป็น null และ orderStatus มี "Pending"
                        if (order != null && order.orderStatus?.contains("Pending") == true) {
                            listOfOrderItem.add(order)
                        }
                    } catch (e: Exception) {
                        Log.e("FirebaseData", "Error parsing data: ${e.message}")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PendingOrderActivity, "โหลดข้อมูลล้มเหลว", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseData", "Error: ${error.message}")
            }
        })
    }



    private fun updateOrderStatus(order: OrderDetails, status: String) {
        val databaseRef = database.reference.child("orders").child(order.currentTime.toString())

        databaseRef.child("orderStatus").setValue(status)
            .addOnSuccessListener {
                Toast.makeText(this, "อัปเดตเป็น: $status", Toast.LENGTH_SHORT).show()

                // ✅ ค้นหา index ของออเดอร์ที่อัปเดต
                val index = listOfOrderItem.indexOfFirst { it.currentTime == order.currentTime }
                if (index != -1) {
                    listOfOrderItem[index].orderStatus = mutableListOf(status) // ✅ เปลี่ยนเป็น mutableListOf()
                    adapter.notifyItemChanged(index) // ✅ อัปเดตเฉพาะรายการเดียว
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "อัปเดตไม่สำเร็จ", Toast.LENGTH_SHORT).show()
            }
    }

    private fun moveToOutForDelivery(order: OrderDetails) {
        val databaseRef = database.reference
        val orderKey = order.currentTime.toString()

        // ✅ อัปเดตสถานะเป็น "OutForDelivery"
        databaseRef.child("orders").child(orderKey).child("orderStatus").setValue("OutForDelivery")
            .addOnSuccessListener {
                // ✅ ย้ายข้อมูลไปที่ "OutForDelivery"
                databaseRef.child("OutForDelivery").child(orderKey).setValue(order)
                    .addOnSuccessListener {
                        // ✅ ลบออเดอร์จาก "orders" หลังจากย้ายไปแล้ว
                        databaseRef.child("orders").child(orderKey).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Order moved to OutForDelivery", Toast.LENGTH_SHORT).show()

                                // ✅ อัปเดต UI โดยลบจากรายการและรีเฟรช
                                val index = listOfOrderItem.indexOfFirst { it.currentTime == order.currentTime }
                                if (index != -1) {
                                    listOfOrderItem.removeAt(index)
                                    adapter.notifyItemRemoved(index)
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "ลบคำสั่งซื้อไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "ย้ายคำสั่งซื้อไป OutForDelivery ไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}
