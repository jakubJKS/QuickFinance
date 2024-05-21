package com.example.loginsignupsql

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupsql.databinding.ActivityCustomizeAccountBinding

class CustomizeAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomizeAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI elements
        val usernameEditText: EditText = binding.usernameEditText
        val emailEditText: EditText = binding.emailEditText
        val phoneEditText: EditText = binding.phoneEditText
        val saveButton: Button = binding.saveButton

        // Set save button click listener
        saveButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()

            // Simple validation
            if (username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Save user information (this could be saving to a database or shared preferences)
                Toast.makeText(this, "Information Saved", Toast.LENGTH_SHORT).show()

                // Finish the activity and return to the previous screen
                finish()
            }
        }
    }
}
