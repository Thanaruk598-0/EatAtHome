package com.example.myproject.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myproject.adapter.MenuAdapter
import com.example.myproject.databinding.FragmentSearchBinding
import com.example.myproject.model.MenuItem
import com.google.firebase.database.*

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var menuRef: DatabaseReference

    private val originalMenuItems = mutableListOf<MenuItem>()
    private val filteredMenuItems = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = MenuAdapter(filteredMenuItems, requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance()
        menuRef = database.getReference("menuItems") // ดึงข้อมูลจาก Firebase

        setupSearchView()
        loadMenuItems()

        return binding.root
    }

    private fun loadMenuItems() {
        database = FirebaseDatabase.getInstance()
        val menuRef = database.getReference("menu") // ✅ ตรวจสอบว่า path ตรงกับ Firebase

        menuRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    originalMenuItems.clear()
                    filteredMenuItems.clear()

                    for (data in snapshot.children) {
                        val item = data.getValue(MenuItem::class.java)
                        if (item != null) {
                            originalMenuItems.add(item)
                        }
                    }

                    filteredMenuItems.addAll(originalMenuItems)
                    adapter.notifyDataSetChanged() // ✅ แจ้ง RecyclerView ว่าข้อมูลเปลี่ยน
                } else {
                    Toast.makeText(requireContext(), "ไม่มีข้อมูลเมนู", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "โหลดข้อมูลเมนูล้มเหลว: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    private fun filterMenuItems(query: String) {
        filteredMenuItems.clear()
        originalMenuItems.forEach {
            if (it.foodName?.contains(query, ignoreCase = true) == true) {
                filteredMenuItems.add(it)
            }
        }
        adapter.notifyDataSetChanged()
    }
}
