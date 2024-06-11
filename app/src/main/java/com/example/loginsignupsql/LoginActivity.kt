package com.example.loginsignupsql

import android.content.Context
import android.content.Intent
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Change the status bar color to black
        window.statusBarColor = Color.Black.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        val username = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        val passwordFocusRequester = remember { FocusRequester() }
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
                    value = username.value,
                    onValueChange = { username.value = it },
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
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password", color = Color.White) },
                    colors = textFieldColors,
                    shape = RoundedCornerShape(50.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .focusRequester(passwordFocusRequester),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Button(
                    onClick = { loginDatabase(username.value, password.value, context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "LOGIN", fontSize = 18.sp, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(5.dp))
                TextButton(
                    onClick = {
                        val intent = Intent(context, SignupActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 35.dp)
                ) {
                    Text(text = "Not registered? Signup", color = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // Replace with your actual logo image resource ID
                    contentDescription = null,
                    modifier = Modifier
                        .height(300.dp)
                        .padding(bottom = 20.dp)
                )
            }
        }
    }

    private fun loginDatabase(username: String, password: String, context: Context) {
        val databaseHelper = DatabaseHelper(context)
        val userExists = databaseHelper.readUser(username, password)
        if (userExists) {
            val sharedPreferences = context.getSharedPreferences("com.example.loginsignupsql.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("USERNAME", username)
                putString("PASSWORD", password)
                apply()
            }

            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }
}