package com.example.loginsignupsql

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupsql.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup card selection spinner
        val cardSelectionSpinner: Spinner = findViewById(R.id.cardSelectionSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.card_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            cardSelectionSpinner.adapter = adapter
        }

        // Setup account customization button
        val accountCustomizationButton: Button = findViewById(R.id.accountCustomizationButton)
        accountCustomizationButton.setOnClickListener {
            // Handle account customization action
            startActivity(Intent(this, CustomizeAccountActivity::class.java))
        }

        // Example code to set the account balance
        val accountBalanceTextView: TextView = findViewById(R.id.accountBalanceTextView)
        accountBalanceTextView.text = "Account Balance: $1234.56"
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        // Prevent the back button from closing the app
        Toast.makeText(this, "Use the Home button to exit the app", Toast.LENGTH_SHORT).show()
    }
}
