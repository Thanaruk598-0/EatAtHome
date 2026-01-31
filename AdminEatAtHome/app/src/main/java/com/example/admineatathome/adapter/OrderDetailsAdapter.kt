package com.example.admineatathome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admineatathome.databinding.OrderDetailItemBinding

class OrderDetailsAdapter(
    private val foodNames: List<String>,
    private val foodPrices: List<String>,
    private val foodQuantities: List<Int>
) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding = OrderDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(foodNames[position], foodPrices[position], foodQuantities[position])
    }

    override fun getItemCount(): Int = foodNames.size

    inner class OrderDetailsViewHolder(private val binding: OrderDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(foodName: String, foodPrice: String, foodQuantity: Int) {
            binding.foodName.text = foodName
            binding.foodPrice.text = "$foodPrice ฿"
            binding.pendingOrderQuantity.text = "Quantity: $foodQuantity"
        }
    }
}
