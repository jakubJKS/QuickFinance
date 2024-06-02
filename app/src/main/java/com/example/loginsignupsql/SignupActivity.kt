package com.example.loginsignupsql

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.loginsignupsql.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var databaseHelper: DatabaseHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("SignupActivity", "Notification permission granted")
            NotificationUtils.showSignupNotification(this)
        } else {
            Log.d("SignupActivity", "Notification permission denied")
            showMessage("Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavenie farby status baru
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }

        databaseHelper = DatabaseHelper(this)

        NotificationUtils.createNotificationChannel(this)

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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("SignupActivity", "Permission already granted, showing notification")
                NotificationUtils.showSignupNotification(this)
            } else {
                Log.d("SignupActivity", "Requesting notification permission")
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
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
