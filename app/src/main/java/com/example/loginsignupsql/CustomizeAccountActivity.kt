package com.example.loginsignupsql

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupsql.databinding.ActivityCustomizeAccountBinding

class CustomizeAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomizeAccountBinding
    private lateinit var databaseHelper: DatabaseHelper
    private val sharedPrefFile = "com.example.loginsignupsql.PREFERENCE_FILE_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        val sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val loggedInUsername = sharedPreferences.getString("USERNAME", null)

        if (loggedInUsername == null) {
            Log.e("CustomizeAccountActivity", "No user logged in")
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserData(loggedInUsername, sharedPreferences)

        binding.saveButton.setOnClickListener {
            saveUserData(loggedInUsername, sharedPreferences)
        }
    }

    private fun loadUserData(username: String, sharedPreferences: SharedPreferences) {
        try {
            val userInfo = databaseHelper.getUserInformation(username)
            if (userInfo != null) {
                binding.usernameEditText.setText(username)
                binding.passwordEditText.setText(sharedPreferences.getString("PASSWORD", ""))
                binding.fullnameEditText.setText(userInfo.getAsString("fullname"))
                binding.emailEditText.setText(userInfo.getAsString("email"))
                binding.phoneEditText.setText(userInfo.getAsString("phone"))
                binding.addressEditText.setText(userInfo.getAsString("address"))
            } else {
                Log.e("CustomizeAccountActivity", "No user info found for username = $username")
            }
        } catch (e: Exception) {
            Log.e("CustomizeAccountActivity", "Error loading user data", e)
        }
    }

    private fun saveUserData(username: String, sharedPreferences: SharedPreferences) {
        val password = binding.passwordEditText.text.toString()
        val fullname = binding.fullnameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()
        val address = binding.addressEditText.text.toString()

        if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            databaseHelper.updateUserInformation(username, fullname, email, phone, address)
            Toast.makeText(this, "Information Saved", Toast.LENGTH_SHORT).show()

            with(sharedPreferences.edit()) {
                putString("PASSWORD", password)
                apply()
            }

            finish()
        } catch (e: Exception) {
            Log.e("CustomizeAccountActivity", "Error saving user data", e)
            Toast.makeText(this, "Error saving information: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}