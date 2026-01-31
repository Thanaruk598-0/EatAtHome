package com.example.myproject.Fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.R
import com.example.myproject.adapter.BuyAgainAdapter
import com.example.myproject.databinding.FragmentHistoryBinding
import com.example.myproject.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private val foodNames = arrayListOf<String>()
    private val foodPrices = arrayListOf<String>()
    private val foodImages = arrayListOf<Int>()
    private val orderStatus = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView()
        loadOrderHistory()

        return binding.root
    }

    private fun setupRecyclerView() {
        buyAgainAdapter = BuyAgainAdapter(foodNames, foodPrices, foodImages, orderStatus)
        binding.BuyAgainRecyclerView.adapter = buyAgainAdapter
        binding.BuyAgainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadOrderHistory() {
        val user = auth.currentUser ?: return
        val userUid = user.uid

        database.child("orders")
            .orderByChild("userUid")
            .equalTo(userUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    foodNames.clear()
                    foodPrices.clear()
                    foodImages.clear()
                    orderStatus.clear()

                    if (!snapshot.exists()) {
                        Log.e("HistoryFragment", "No orders found for user: $userUid")
                        return
                    }

                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderDetails::class.java)
                        if (order != null) {
                            foodNames.addAll(order.getFoodNamesList())
                            foodPrices.addAll(order.getFoodPricesList())
                            orderStatus.addAll(order.orderStatus as List<String>)

                            val imageCount = order.getFoodNamesList().size
                            if (imageCount > 0) {
                                val imageList = MutableList(imageCount) { R.drawable.menu6 }
                                foodImages.addAll(imageList)
                            }
                        }
                    }

                    // ✅ อัปเดตสีของ `orderStatus`
                    updateOrderStatusUI()
                    buyAgainAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HistoryFragment", "Failed to load order history: ${error.message}")
                }
            })
    }

    private fun updateOrderStatusUI() {
        val isDelivered = orderStatus.contains("Delivered")
        binding.orderStatus.setCardBackgroundColor(
            if (isDelivered) Color.parseColor("#0F782B") // สีเขียว: จัดส่งแล้ว
            else Color.parseColor("#D62E2E") // สีแดง: กำลังรอ
        )
    }
}
