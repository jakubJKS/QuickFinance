package com.example.loginsignupsql

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupsql.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        binding.signupButton.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()
            val repeatPassword = binding.repeatPassword.text.toString()

            if (!isUsernameValid(signupUsername)) {
                showMessage("Username must be at least 5 characters long")
                return@setOnClickListener
            }

            if (!isPasswordValid(signupPassword)) {
                showMessage("Password must be at least 5 characters long and contain at least one number")
                return@setOnClickListener
            }

            if (signupPassword != repeatPassword) {
                showMessage("Passwords do not match")
                return@setOnClickListener
            }

            if (databaseHelper.isUserExists(signupUsername)) {
                showMessage("User already exists")
            } else {
                signupDatabase(signupUsername, signupPassword)
            }
        }

        // Setup listeners for each EditText to handle the Enter key for focusing the next EditText
        setupEditorActionListeners()

        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupEditorActionListeners() {
        binding.signupUsername.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.signupUsername.clearFocus()
                binding.signupPassword.requestFocus()
                true
            } else false
        }

        binding.signupPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.signupPassword.clearFocus()
                binding.repeatPassword.requestFocus()
                true
            } else false
        }

        binding.repeatPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.repeatPassword.clearFocus()
                hideKeyboard()
                true
            } else false
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.repeatPassword.windowToken, 0)
    }

    private fun signupDatabase(username: String, password: String) {
        val insertedRowId = databaseHelper.insertUser(username, password)
        if (insertedRowId != -1L) {
            showMessage("Signup Successful")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            showMessage("Signup Failed")
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.length >= 5
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 5 && password.any { it.isDigit() }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
