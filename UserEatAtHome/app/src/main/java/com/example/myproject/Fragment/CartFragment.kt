package com.example.myproject.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.PayOutActivity
import com.example.myproject.adapter.CartAdapter
import com.example.myproject.databinding.FragmentCartBinding
import com.example.myproject.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView() // ✅ เรียกก่อน loadCartItems()
        loadCartItems()

        binding.proceedButton.setOnClickListener {
            val totalPrice = calculateTotalPrice()
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("TOTAL_CART_PRICE", totalPrice)
            startActivity(intent)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems) { cartItem ->
            deleteCartItem(cartItem)
        }
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun loadCartItems() {
        val user = auth.currentUser
        if (user != null) {
            val emailKey = user.email?.replace(".", "_")?.replace("@", "_") ?: ""

            database.child("Users").child(emailKey).child("CartItems")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        cartItems.clear()
                        for (cartSnapshot in snapshot.children) {
                            val cartItem = cartSnapshot.getValue(CartItem::class.java)
                            cartItem?.let {
                                cartItems.add(it)
                            }
                        }
                        cartAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("CartFragment", "Firebase Error: ${error.message}")
                    }
                })
        }
    }

    private fun deleteCartItem(cartItem: CartItem) {
        val user = auth.currentUser
        if (user != null) {
            val emailKey = user.email?.replace(".", "_")?.replace("@", "_") ?: ""
            database.child("Users").child(emailKey).child("CartItems")
                .orderByChild("foodName").equalTo(cartItem.foodName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (cartSnapshot in snapshot.children) {
                            cartSnapshot.ref.removeValue()
                        }
                        cartItems.remove(cartItem)
                        cartAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("CartFragment", "Failed to delete item: ${error.message}")
                    }
                })
        }
    }

    private fun calculateTotalPrice(): Int {
        var total = 0
        for (cartItem in cartItems) {
            val price = cartItem.foodPrice?.toIntOrNull() ?: 0
            total += price * cartItem.foodQuantity
        }
        return total
    }
}
