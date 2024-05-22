package com.example.loginsignupsql

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupsql.databinding.ActivityKontaktyBinding

class KontaktyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKontaktyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKontaktyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bottom navigation buttons
        binding.overviewButton.setOnClickListener {
            // Navigate to overview
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.contactsButton.setOnClickListener {
            // Already on contacts, no action needed
        }
    }
}
