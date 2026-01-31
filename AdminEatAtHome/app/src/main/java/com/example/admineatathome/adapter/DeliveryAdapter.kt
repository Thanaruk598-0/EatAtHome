package com.example.admineatathome.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admineatathome.databinding.DeliveryItemBinding
import com.example.admineatathome.model.OrderDetails

class DeliveryAdapter(
    private val orderList: MutableList<OrderDetails>,
    private val context: Context,
    private val onStatusUpdate: (OrderDetails) -> Unit // ✅ Callback ไปอัปเดตสถานะใน Firebase
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(orderList[position], position)
    }

    override fun getItemCount(): Int = orderList.size

    inner class DeliveryViewHolder(private val binding: DeliveryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderDetails, position: Int) {
            binding.customerName.text = order.userName ?: "Unknown"
            binding.moneyStatus.text = order.orderStatus.joinToString(", ") // ✅ แสดงสถานะล่าสุด

            // ✅ ตรวจสอบว่า orderStatus เป็น "Delivered" หรือไม่
            val isDelivered = order.orderStatus.contains("Delivered")

            // ✅ กำหนดสีของ CardView ตามสถานะ
            binding.statusColor.setCardBackgroundColor(
                if (isDelivered) Color.parseColor("#0F782B") // สีเขียว
                else Color.parseColor("#D62E2E") // สีแดง
            )

            binding.deliveryButton.apply {
                setOnCheckedChangeListener(null) // ✅ ป้องกันการเรียกซ้ำ
                isChecked = isDelivered
                text = if (isDelivered) "Delivered" else "Delivery"
                isEnabled = !isDelivered // ✅ ถ้าส่งของแล้ว ให้ปิดปุ่ม

                setOnCheckedChangeListener { _, checked ->
                    if (checked && !isDelivered) {
                        order.orderStatus = mutableListOf("Delivered") // ✅ อัปเดตค่าในอ็อบเจ็กต์
                        onStatusUpdate(order) // ✅ Callback ไปอัปเดตสถานะใน Firebase

                        // ✅ เปลี่ยนสีของ CardView เป็นสีเขียว
                        binding.statusColor.setCardBackgroundColor(Color.parseColor("#0F782B"))

                        notifyItemChanged(position) // ✅ รีเฟรช UI เฉพาะรายการที่เปลี่ยน
                    }
                }
            }
        }
    }
}
