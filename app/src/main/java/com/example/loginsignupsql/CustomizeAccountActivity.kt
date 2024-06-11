package com.example.loginsignupsql

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme

class CustomizeAccountActivity : ComponentActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private val sharedPrefFile = "com.example.loginsignupsql.PREFERENCE_FILE_KEY"
    private val channelId = "account_update_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Change the status bar color
        window.statusBarColor = Color(0xFF121212).toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        databaseHelper = DatabaseHelper(this) // Initialize the database helper

        createNotificationChannel()

        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                    CustomizeAccountScreen()
                }
            }
        }
    }

    @Composable
    fun CustomizeAccountScreen() {
        val context = LocalContext.current
        val sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        var username by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var fullname by rememberSaveable { mutableStateOf("") }
        var email by rememberSaveable { mutableStateOf("") }
        var phone by rememberSaveable { mutableStateOf("") }
        var address by rememberSaveable { mutableStateOf("") }

        val focusManager = LocalFocusManager.current
        val focusRequesterFullname = remember { FocusRequester() }
        val focusRequesterEmail = remember { FocusRequester() }
        val focusRequesterPhone = remember { FocusRequester() }
        val focusRequesterAddress = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            val loggedInUsername = sharedPreferences.getString("USERNAME", null)
            val loggedInPassword = sharedPreferences.getString("PASSWORD", null)
            if (loggedInUsername == null || loggedInPassword == null) {
                Log.e("CustomizeAccountActivity", "No user logged in")
                Toast.makeText(context, "No user logged in", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                username = loggedInUsername
                password = loggedInPassword
                loadUserData(
                    username,
                    onFullnameLoaded = { fullname = it },
                    onEmailLoaded = { email = it },
                    onPhoneLoaded = { phone = it },
                    onAddressLoaded = { address = it }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))  // Add padding at the top
            Text(
                text = "Customize Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))  // Add padding after title
            CustomTextField(
                label = "Username",
                value = username,
                onValueChange = {},
                enabled = false,
                imeAction = ImeAction.Next,
                focusRequester = FocusRequester.Default
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Password",
                value = password,
                onValueChange = {},
                enabled = false,
                imeAction = ImeAction.Next,
                focusRequester = FocusRequester.Default
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Full Name",
                value = fullname,
                onValueChange = { fullname = it },
                imeAction = ImeAction.Next,
                focusRequester = focusRequesterFullname,
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterEmail.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                imeAction = ImeAction.Next,
                focusRequester = focusRequesterEmail,
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterPhone.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Phone",
                value = phone,
                onValueChange = { phone = it },
                imeAction = ImeAction.Next,
                focusRequester = focusRequesterPhone,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterAddress.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Address",
                value = address,
                onValueChange = { address = it },
                imeAction = ImeAction.Done,
                focusRequester = focusRequesterAddress,
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    Log.d("CustomizeAccountActivity", "Save clicked: $fullname, $email, $phone, $address")
                    saveUserData(username, password, fullname, email, phone, address, sharedPreferences)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Save", color = Color.Black)
            }
        }
    }

    @Composable
    fun CustomTextField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean = true,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        imeAction: ImeAction = ImeAction.Default,
        focusRequester: FocusRequester
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                enabled = enabled,
                keyboardOptions = keyboardOptions.copy(imeAction = imeAction),
                keyboardActions = keyboardActions
            )
        }
    }

    private fun loadUserData(
        username: String,
        onFullnameLoaded: (String) -> Unit,
        onEmailLoaded: (String) -> Unit,
        onPhoneLoaded: (String) -> Unit,
        onAddressLoaded: (String) -> Unit
    ) {
        try {
            val userInfo = databaseHelper.getUserInformation(username)
            if (userInfo != null) {
                onFullnameLoaded(userInfo.getAsString("fullname"))
                onEmailLoaded(userInfo.getAsString("email"))
                onPhoneLoaded(userInfo.getAsString("phone"))
                onAddressLoaded(userInfo.getAsString("address"))
            } else {
                Log.e("CustomizeAccountActivity", "No user info found for username = $username")
            }
        } catch (e: Exception) {
            Log.e("CustomizeAccountActivity", "Error loading user data", e)
        }
    }

    private fun saveUserData(
        username: String,
        password: String,
        fullname: String,
        email: String,
        phone: String,
        address: String,
        sharedPreferences: SharedPreferences
    ) {
        try {
            Log.d("CustomizeAccountActivity", "Saving: $fullname, $email, $phone, $address")
            if (fullname.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return
            }

            databaseHelper.updateUserInformation(username, fullname, email, phone, address)
            with(sharedPreferences.edit()) {
                putString("USERNAME", username)
                putString("PASSWORD", password)
                putString("FULLNAME", fullname)
                putString("EMAIL", email)
                putString("PHONE", phone)
                putString("ADDRESS", address)
                apply()
            }

            Toast.makeText(this, "Information Saved", Toast.LENGTH_SHORT).show()
            sendNotification(this)
            finish()
        } catch (e: Exception) {
            Log.e("CustomizeAccountActivity", "Error saving user data", e)
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Account Updates"
            val descriptionText = "Notifications for account updates"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Ensure you have an icon drawable
            .setContentTitle("Account Updated")
            .setContentText("Your account information has been updated successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set high priority to show heads-up notification
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Make the notification public

        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notify(1, builder.build())
            } else {
                // Request permission from the user
                androidx.core.app.ActivityCompat.requestPermissions(
                    this@CustomizeAccountActivity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }
}
