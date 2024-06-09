package com.example.loginsignupsql.PrehladAKontaktyMenu

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.loginsignupsql.DatabaseHelper
import com.example.loginsignupsql.HomeActivity
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme

class KontaktyActivity : ComponentActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                    KontaktyScreen()
                }
            }
        }

        // Set the status bar color to match the background
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false // for dark text color
        window.statusBarColor = Color(0xFF121212).toArgb()

        databaseHelper = DatabaseHelper(this)
        val username = intent.getStringExtra("username")
        if (username != null) {
            userId = databaseHelper.getUserId(username)
        } else {
            Toast.makeText(this, "Username not found, returning to previous screen.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @Composable
    fun KontaktyScreen() {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        val firstnameFocusRequester = remember { FocusRequester() }
        val lastnameFocusRequester = remember { FocusRequester() }
        val ibanFocusRequester = remember { FocusRequester() }

        var firstname by remember { mutableStateOf("") }
        var lastname by remember { mutableStateOf("") }
        var iban by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {  // Using Box to wrap components
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color(0xFF121212)),  // Set background color here
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(text = "Pridaj nový kontakt", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Spacer(modifier = Modifier.height(32.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "First Name", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                    BasicTextField(
                        value = firstname,
                        onValueChange = { firstname = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray)
                            .padding(16.dp)
                            .focusRequester(firstnameFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { lastnameFocusRequester.requestFocus() }
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Last Name", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                    BasicTextField(
                        value = lastname,
                        onValueChange = { lastname = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray)
                            .padding(16.dp)
                            .focusRequester(lastnameFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { ibanFocusRequester.requestFocus() }
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "IBAN", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                    BasicTextField(
                        value = iban,
                        onValueChange = { iban = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray)
                            .padding(16.dp)
                            .focusRequester(ibanFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (firstname.isNotEmpty() && lastname.isNotEmpty() && iban.isNotEmpty()) {
                            databaseHelper.insertContact(firstname, lastname, iban, userId)
                            firstname = ""
                            lastname = ""
                            iban = ""
                        } else {
                            Toast.makeText(this@KontaktyActivity, "Prosím, vyplňte všetky polia", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
                ) {
                    Text("Save Contact", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(80.dp)) // Extra space to make sure content is scrollable above the buttons
            }

            // Scrolling ContactsList
            ContactsList(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 300.dp, bottom = 100.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            )

            // Row for buttons placed at the bottom of the screen
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xFF121212))
                    .padding(bottom = 50.dp) // Adjust padding to avoid overlap with the system navigation bar
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val interactionSource1 = remember { MutableInteractionSource() }
                val interactionSource2 = remember { MutableInteractionSource() }

                Button(
                    onClick = {
                        startActivity(Intent(context, HomeActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                        .clickable(interactionSource = interactionSource1, indication = rememberRipple(bounded = true)) { }
                ) {
                    Text("Prehľad", color = Color.Black)
                }
                Button(
                    onClick = { /* No action needed, already on Kontakty */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .clickable(interactionSource = interactionSource2, indication = rememberRipple(bounded = true)) { }
                ) {
                    Text("Kontakty", color = Color.Black)
                }
            }
        }
    }

    @Composable
    fun ContactsList(modifier: Modifier = Modifier) {
        val cursor: Cursor = databaseHelper.getAllContacts(userId)
        val contacts = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val firstname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTACT_FIRSTNAME))
            val lastname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTACT_LASTNAME))
            val iban = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTACT_IBAN))
            contacts.add("$firstname $lastname - $iban")
        }
        cursor.close()

        Column(modifier = modifier) {
            contacts.forEachIndexed { index, contact ->
                if (index > 0) {
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
                Text(text = contact, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
