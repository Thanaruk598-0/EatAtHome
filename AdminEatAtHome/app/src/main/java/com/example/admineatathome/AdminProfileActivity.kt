package com.example.admineatathome

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.admineatathome.databinding.ActivityAdminProfileBinding
import com.example.admineatathome.databinding.ActivityAllItemBinding

class AdminProfileActivity : AppCompatActivity() {

    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backbtn.setOnClickListener {
            finish()
        }

        val locationList = arrayListOf(
            "Khon Kaen",
            "Maha Sarakham",
            "Chonburi"
        )

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

        binding.textInputLayout.isEnabled = false
        binding.restaurantName.isEnabled = false
        binding.edtAddress.isEnabled = false
        binding.edtPhone.isEnabled = false
        binding.emailOrPhone.isEnabled = false
        binding.password.isEnabled = false
        binding.selectImage.isEnabled = false

        var isEnable = false
        binding.editButton.setOnClickListener {
            isEnable = !isEnable
            binding.textInputLayout.isEnabled = isEnable
            binding.restaurantName.isEnabled = isEnable
            binding.edtAddress.isEnabled = isEnable
            binding.edtPhone.isEnabled = isEnable
            binding.emailOrPhone.isEnabled = isEnable
            binding.password.isEnabled = isEnable
            binding.selectImage.isEnabled = isEnable
            binding.selectImage.setOnClickListener {
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            if (isEnable) {
                binding.restaurantName.requestFocus()
            }
        }
    }

    val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.selectedImage.setImageURI(uri)
        }
    }

}