package com.example.myproject.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myproject.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.button7.setOnClickListener {
            saveUserProfile()
        }

        return binding.root
    }

    private fun saveUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val emailKey = user.email?.replace(".", "_")?.replace("@", "_") ?: ""
            val userProfile = mapOf(
                "name" to binding.editText.text.toString(),
                "address" to binding.editText97.text.toString(),
                "email" to binding.editText99.text.toString(),
                "phone" to binding.editText98.text.toString()
            )

            database.child("Users").child(emailKey).child("Profile")
                .setValue(userProfile)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Error saving profile: ${e.message}")
                }
        }
    }
}
