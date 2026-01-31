package com.example.admineatathome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.admineatathome.databinding.PendingOrderItemBinding
import com.example.admineatathome.model.OrderDetails

class PendingOrderAdapter(
    private val orderList: MutableList<OrderDetails>,
    private val context: Context,
    private val onOrderAction: (OrderDetails, String) -> Unit,
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding =
            PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size

    inner class PendingOrderViewHolder(private val binding: PendingOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderDetails) {
            binding.customerName.text = order.userName ?: "Unknown"
            binding.pendingOrderQuantity.text = order.foodQuantities?.sum().toString()

            binding.foodName.text = order.foodNames?.joinToString(", ") ?: "No food name"

            // ✅ ตรวจสอบสถานะออเดอร์เพื่อเปลี่ยนปุ่ม
            binding.btnaccept.apply {
                text = if (order.orderStatus?.contains("Accepted") == true) "Dispatch" else "Accept"

                setOnClickListener {
                    if (order.orderStatus?.contains("Pending") == true) {
                        onOrderAction(order, "accept") // ✅ กด Accept แล้วต้องเปลี่ยนเป็น Dispatch
                    } else if (order.orderStatus?.contains("Accepted") == true) {
                        onOrderAction(order, "dispatch") // ✅ เมื่อกด Dispatch จะย้ายไป OutForDelivery
                    }
                }
            }
        }
    }
}

