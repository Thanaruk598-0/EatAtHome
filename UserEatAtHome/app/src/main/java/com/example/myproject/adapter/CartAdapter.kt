package com.example.myproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myproject.databinding.CartItemBinding
import com.example.myproject.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onDeleteClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        if (position < cartItems.size) {
            holder.bind(cartItems[position], position)
        } else {
            Log.e("CartAdapter", "Attempted to bind empty cartItems at position: $position")
        }
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem, position: Int) {
            binding.apply {
                val basePrice = cartItem.foodPrice?.toDoubleOrNull() ?: 0.0
                val totalPrice = basePrice * cartItem.foodQuantity

                cartitemPrice.text = "${totalPrice.toInt()} THB"
                cardFoodName.text = cartItem.foodName ?: "Unknown Item"
                cartItemQuantity.text = cartItem.foodQuantity.toString()

                Glide.with(binding.root.context).load(cartItem.foodImage).into(cartimage)

                minusbutton.setOnClickListener { updateQuantity(cartItem, position, -1) }
                plusbutton.setOnClickListener { updateQuantity(cartItem, position, 1) }
                deleteButton.setOnClickListener { onDeleteClick(cartItem) }
            }
        }

        private fun updateQuantity(cartItem: CartItem, position: Int, change: Int) {
            val user = FirebaseAuth.getInstance().currentUser
            val database = FirebaseDatabase.getInstance().reference

            if (user != null) {
                val emailKey = user.email?.replace(".", "_")?.replace("@", "_") ?: ""
                val cartRef = database.child("Users").child(emailKey).child("CartItems")

                cartRef.orderByChild("foodName").equalTo(cartItem.foodName)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (cartSnapshot in snapshot.children) {
                                val currentQuantity = cartSnapshot.child("foodQuantity").getValue(Int::class.java) ?: 1
                                val newQuantity = (currentQuantity + change).coerceAtLeast(1)

                                val basePrice = cartItem.foodPrice?.toDoubleOrNull() ?: 0.0
                                val newTotalPrice = basePrice * newQuantity

                                cartSnapshot.ref.child("foodQuantity").setValue(newQuantity)
                                cartSnapshot.ref.child("totalPrice").setValue(newTotalPrice.toInt())

                                cartItem.foodQuantity = newQuantity
                                binding.cartItemQuantity.text = newQuantity.toString()
                                binding.cartitemPrice.text = "${newTotalPrice.toInt()} THB"

                                notifyItemChanged(position)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("CartAdapter", "Failed to update quantity: ${error.message}")
                        }
                    })
            }
        }
    }
}
