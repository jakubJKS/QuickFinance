package com.example.loginsignupsql.com.example.loginsignupsql.PrehladAKontaktyMenu

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.loginsignupsql.DatabaseHelper
import com.example.loginsignupsql.R
import com.example.loginsignupsql.databinding.ActivityKontaktyBinding
import com.example.loginsignupsql.databinding.DialogContactDetailBinding

class KontaktyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKontaktyBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var contactsAdapter: SimpleCursorAdapter
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("KontaktyActivity", "onCreate called")
        binding = ActivityKontaktyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavenie farby status baru
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }

        databaseHelper = DatabaseHelper(this)
        Log.d("KontaktyActivity", "DatabaseHelper initialized")

        val username = intent.getStringExtra("username")
        if (username != null) {
            userId = databaseHelper.getUserId(username)
            Log.d("KontaktyActivity", "User ID: $userId")
        } else {
            Log.e("KontaktyActivity", "Username not found in intent")
            Toast.makeText(this, "Username not found, returning to previous screen.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val fromColumns = arrayOf(
            DatabaseHelper.COLUMN_CONTACT_FIRSTNAME,
            DatabaseHelper.COLUMN_CONTACT_LASTNAME,
            DatabaseHelper.COLUMN_CONTACT_IBAN
        )
        val toViews = intArrayOf(
            R.id.firstNameTextView,
            R.id.lastNameTextView,
            R.id.ibanTextView
        )

        contactsAdapter = SimpleCursorAdapter(
            this,
            R.layout.contact_list_item,
            null,
            fromColumns,
            toViews,
            0
        )

        binding.contactsListView.adapter = contactsAdapter
        Log.d("KontaktyActivity", "Contacts adapter set")

        loadContacts()

        binding.addContactButton.setOnClickListener {
            val firstname = binding.firstnameEditText.text.toString()
            val lastname = binding.lastnameEditText.text.toString()
            val iban = binding.ibanEditText.text.toString()
            if (firstname.isNotEmpty() && lastname.isNotEmpty() && iban.isNotEmpty()) {
                Log.d("KontaktyActivity", "Adding contact: $firstname $lastname $iban")
                databaseHelper.insertContact(firstname, lastname, iban, userId)
                loadContacts()
                binding.firstnameEditText.text.clear()
                binding.lastnameEditText.text.clear()
                binding.ibanEditText.text.clear()
            } else {
                Toast.makeText(this, "Prosím, vyplňte všetky polia", Toast.LENGTH_SHORT).show()
            }
        }

        binding.contactsListView.setOnItemLongClickListener { parent, view, position, id ->
            Log.d("KontaktyActivity", "Deleting contact with ID: $id")
            databaseHelper.deleteContact(id)
            loadContacts()
            true
        }

        binding.contactsListView.setOnItemClickListener { parent, view, position, id ->
            val cursor = contactsAdapter.getItem(position) as Cursor
            val firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTACT_FIRSTNAME))
            val lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTACT_LASTNAME))
            val iban = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTACT_IBAN))
            showContactDetail(firstName, lastName, iban)
        }

        binding.bottomNavigation.findViewById<Button>(R.id.overviewButton).setOnClickListener {
            finish()
        }

        binding.bottomNavigation.findViewById<Button>(R.id.contactsButton).setOnClickListener {
            // Už sme na Kontakty, žiadna akcia nepotrebná
        }
    }

    private fun loadContacts() {
        Log.d("KontaktyActivity", "Loading contacts")
        val cursor = databaseHelper.getAllContacts(userId)
        contactsAdapter.changeCursor(cursor)
    }

    private fun showContactDetail(firstName: String, lastName: String, iban: String) {
        val dialogBinding = DialogContactDetailBinding.inflate(LayoutInflater.from(this))
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogBinding.root)
        dialogBuilder.setTitle("Detail Kontakt")

        dialogBinding.firstNameTextView.text = firstName
        dialogBinding.lastNameTextView.text = lastName
        dialogBinding.ibanTextView.text = iban

        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(ContextCompat.getColor(context, R.color.yellow))
        }
    }


}
