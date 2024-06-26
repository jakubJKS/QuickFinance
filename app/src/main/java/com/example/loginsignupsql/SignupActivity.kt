package com.example.loginsignupsql

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme

class SignupActivity : ComponentActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private val channelId = "signup_success_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Change the status bar color to black
        window.statusBarColor = Color.Black.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        databaseHelper = DatabaseHelper(this)

        createNotificationChannel()

        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SignupScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SignupScreen() {
        val context = LocalContext.current
        val signupUsername = rememberSaveable { mutableStateOf("") }
        val signupPassword = rememberSaveable { mutableStateOf("") }
        val repeatPassword = rememberSaveable { mutableStateOf("") }
        val passwordFocusRequester = remember { FocusRequester() }
        val repeatPasswordFocusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.login1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val textFieldColors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Yellow,
                    unfocusedIndicatorColor = Color.Yellow,
                    focusedLabelColor = Color.Yellow,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Yellow,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )

                TextField(
                    value = signupUsername.value,
                    onValueChange = { signupUsername.value = it },
                    label = { Text("Username", color = Color.White) },
                    colors = textFieldColors,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                TextField(
                    value = signupPassword.value,
                    onValueChange = { signupPassword.value = it },
                    label = { Text("Password", color = Color.White) },
                    colors = textFieldColors,
                    shape = RoundedCornerShape(50.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .focusRequester(passwordFocusRequester),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { repeatPasswordFocusRequester.requestFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                TextField(
                    value = repeatPassword.value,
                    onValueChange = { repeatPassword.value = it },
                    label = { Text("Repeat Password", color = Color.White) },
                    colors = textFieldColors,
                    shape = RoundedCornerShape(50.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .focusRequester(repeatPasswordFocusRequester),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Button(
                    onClick = { signupDatabase(signupUsername.value, signupPassword.value, repeatPassword.value, context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "SIGNUP", fontSize = 18.sp, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(5.dp))
                TextButton(
                    onClick = {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 35.dp)
                ) {
                    Text(text = "Already registered? Login", color = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .height(300.dp)
                        .padding(bottom = 20.dp)
                )
            }
        }
    }

    private fun signupDatabase(username: String, password: String, repeatPassword: String, context: Context) {
        if (username.length >= 5 && password.length >= 5 && password.any { it.isDigit() }) {
            if (password == repeatPassword) {
                databaseHelper = DatabaseHelper(context)
                if (databaseHelper.isUserExists(username)) {
                    Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    val insertedRowId = databaseHelper.insertUser(username, password)
                    if (insertedRowId != -1L) {
                        Toast.makeText(context, "Signup Successful", Toast.LENGTH_SHORT).show()
                        sendNotification(context)
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Signup Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Signup Success"
            val descriptionText = "Notifications for successful signups"
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
            .setContentTitle("Signup Successful")
            .setContentText("Your account has been created successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set high priority to show heads-up notification
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // To ensure it appears on lock screen

        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notify(1, builder.build())
            } else {
                // Request permission from the user
                androidx.core.app.ActivityCompat.requestPermissions(
                    this@SignupActivity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }
}
