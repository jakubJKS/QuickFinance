package com.example.loginsignupsql

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupsql.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseHelper: DatabaseHelper
    private val sharedPrefFile = "com.example.loginsignupsql.PREFERENCE_FILE_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        binding.loginButton.setOnClickListener {
            val loginUsername = binding.loginUsername.text.toString()
            val loginPassword = binding.loginPassword.text.toString()
            loginDatabase(loginUsername, loginPassword)
        }

        // Add setOnEditorActionListener to loginUsername EditText
        binding.loginUsername.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == KeyEvent.KEYCODE_ENTER || keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.loginPassword.requestFocus() // Move focus to password EditText
                return@OnEditorActionListener true
            }
            false
        })

        binding.signupRedirect.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginDatabase(username: String, password: String) {
        val userExists = databaseHelper.readUser(username, password)
        if (userExists) {
            // Save login credentials in SharedPreferences
            val sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("USERNAME", username)
                putString("PASSWORD", password)
                apply()
            }

            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }

}
