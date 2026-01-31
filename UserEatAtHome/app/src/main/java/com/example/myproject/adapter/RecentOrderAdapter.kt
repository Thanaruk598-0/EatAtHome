package com.example.myproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myproject.databinding.RecentBuyItemBinding

class RecentOrderAdapter(
    private val foodNames: List<String>,
    private val foodPrices: List<String>,
    private val foodImages: List<Int>,
    private val foodQuantities: List<String>
) : RecyclerView.Adapter<RecentOrderAdapter.ViewHolder>() {

    class ViewHolder(val binding: RecentBuyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecentBuyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.foodName.text = foodNames[position]
        holder.binding.foodPrice.text = foodPrices[position]
        holder.binding.foodQuantity.text = "Quantity: ${foodQuantities[position]}"
        holder.binding.foodImage.setImageResource(foodImages[position])
    }

    override fun getItemCount(): Int {
        return foodNames.size
    }
}



