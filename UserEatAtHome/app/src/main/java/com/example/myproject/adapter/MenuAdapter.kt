package com.example.myproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myproject.DetailsActivity
import com.example.myproject.databinding.MenuItemBinding
import com.example.myproject.model.MenuItem
import com.google.firebase.database.FirebaseDatabase

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(menuItems[position])
                }
            }
        }

        private fun openDetailsActivity(menuItem: MenuItem) {
            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredients", menuItem.foodIngredient)
            }
            context.startActivity(intent)
        }

        fun bind(menuItem: MenuItem) {
            binding.menuFoodName.text = menuItem.foodName
            binding.menuPrice.text = menuItem.foodPrice

            if (menuItem.imageUrl.isNotEmpty()) {
                Glide.with(binding.root.context).load(menuItem.imageUrl).into(binding.menuImage)
            }

            binding.menuAddToCart.setOnClickListener {
                addToCart(menuItem)
            }
        }

        private fun addToCart(menuItem: MenuItem) {
            val cartRef = FirebaseDatabase.getInstance().getReference("cartItems")
            val cartItemId = cartRef.push().key

            cartItemId?.let {
                cartRef.child(it).setValue(menuItem).addOnSuccessListener {
                    Toast.makeText(context, "เพิ่ม ${menuItem.foodName} ลงในตะกร้า", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "เพิ่มสินค้าไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
