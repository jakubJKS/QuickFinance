package com.example.loginsignupsql

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupsql.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var databaseHelper: DatabaseHelper
    private val sharedPrefFile = "com.example.loginsignupsql.PREFERENCE_FILE_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        val sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val loggedInUsername = sharedPreferences.getString("USERNAME", null)

        if (loggedInUsername == null) {
            Log.e("HomeActivity", "No user logged in")
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserData(loggedInUsername)

        // Setup account customization button
        binding.accountCustomizationButton.setOnClickListener {
            // Handle account customization action
            startActivity(Intent(this, CustomizeAccountActivity::class.java))
        }

        // Setup click listeners for horizontal scrollable items
        binding.item1.setOnClickListener {
            // Handle item1 click action
            startActivity(Intent(this, Item1Activity::class.java))
        }
        binding.item2.setOnClickListener {
            // Handle item2 click action
            startActivity(Intent(this, Item2Activity::class.java))
        }
        binding.item3.setOnClickListener {
            // Handle item3 click action
            startActivity(Intent(this, Item3Activity::class.java))
        }
        binding.item4.setOnClickListener {
            // Handle item4 click action
            startActivity(Intent(this, Item4Activity::class.java))
        }
        binding.item5.setOnClickListener {
            // Handle item5 click action
            startActivity(Intent(this, Item5Activity::class.java))
        }
        binding.item6.setOnClickListener {
            // Handle item6 click action
            startActivity(Intent(this, Item6Activity::class.java))
        }

        // Setup click listeners for vertical scrollable boxes
        binding.accountBalanceTextView.setOnClickListener {
            // Handle account balance click action
            startActivity(Intent(this, AccountBalanceActivity::class.java))
        }
        binding.product1.setOnClickListener {
            // Handle product1 click action
            startActivity(Intent(this, Product1Activity::class.java))
        }
        binding.product2.setOnClickListener {
            // Handle product2 click action
            startActivity(Intent(this, Product2Activity::class.java))
        }
        binding.product3.setOnClickListener {
            // Handle product3 click action
            startActivity(Intent(this, Product3Activity::class.java))
        }

        // Bottom navigation buttons
        binding.overviewButton.setOnClickListener {
            // Handle overview action
        }

        binding.contactsButton.setOnClickListener {
            // Handle contacts action
        }
    }

    private fun loadUserData(username: String) {
        try {
            val userInfo = databaseHelper.getUserInformation(username)
            if (userInfo != null) {
                binding.welcomeTextView.text = getString(R.string.welcome_user, userInfo.getAsString("fullname"))
                binding.accountBalanceTextView.text = "Account Balance: ${userInfo.getAsString("balance")}"
            } else {
                Log.e("HomeActivity", "No user info found for username = $username")
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error loading user data", e)
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        // Prevent the back button from closing the app
        Toast.makeText(this, "Use the Home button to exit the app", Toast.LENGTH_SHORT).show()
    }
}
