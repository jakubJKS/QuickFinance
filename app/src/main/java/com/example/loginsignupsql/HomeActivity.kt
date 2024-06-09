package com.example.loginsignupsql

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.loginsignupsql.ItemActivities.AccountBalanceActivity
import com.example.loginsignupsql.ItemActivities.Item1Activity
import com.example.loginsignupsql.ItemActivities.Item2Activity
import com.example.loginsignupsql.ItemActivities.Item3Activity
import com.example.loginsignupsql.ItemActivities.Product1Activity
import com.example.loginsignupsql.ItemActivities.Product2Activity
import com.example.loginsignupsql.ItemActivities.Product3Activity
import com.example.loginsignupsql.PrehladAKontaktyMenu.KontaktyActivity
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Change the status bar color
        window.statusBarColor = Color(0xFF121212).toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                    HomeScreen()
                }
            }
        }
    }

    @Composable
    fun HomeScreen() {
        val context = this@HomeActivity
        val username = remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            val sharedPreferences = getSharedPreferences("com.example.loginsignupsql.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
            username.value = sharedPreferences.getString("USERNAME", "") ?: ""
            if (username.value.isEmpty()) {
                Toast.makeText(context, "No user logged in", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Welcome, ${username.value}", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Yellow,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                startActivity(Intent(this@HomeActivity, CustomizeAccountActivity::class.java))
                            }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.White, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalScrollItems()
                Spacer(modifier = Modifier.height(16.dp))
                VerticalScrollItems(modifier = Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp), // Ensure buttons are always visible
                contentAlignment = Alignment.BottomCenter
            ) {
                BottomButtons(username.value)
            }
        }
    }

    @Composable
    fun HorizontalScrollItems() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            SmallItemBox("Vydavky") { startActivity(Intent(this@HomeActivity, Item1Activity::class.java)) }
            Spacer(modifier = Modifier.width(16.dp))
            SmallItemBox("Byvanie") { startActivity(Intent(this@HomeActivity, Item2Activity::class.java)) }
            Spacer(modifier = Modifier.width(16.dp))
            SmallItemBox("Strava") { startActivity(Intent(this@HomeActivity, Item3Activity::class.java)) }
            // Add more items as needed
        }
    }

    @Composable
    fun VerticalScrollItems(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {
            LargeItemBox("Account Balance: $0.00") { startActivity(Intent(this@HomeActivity, AccountBalanceActivity::class.java)) }
            Spacer(modifier = Modifier.height(16.dp))
            LargeItemBox("Product 1") { startActivity(Intent(this@HomeActivity, Product1Activity::class.java)) }
            Spacer(modifier = Modifier.height(16.dp))
            LargeItemBox("Product 2") { startActivity(Intent(this@HomeActivity, Product2Activity::class.java)) }
            Spacer(modifier = Modifier.height(16.dp))
            LargeItemBox("Product 3") { startActivity(Intent(this@HomeActivity, Product3Activity::class.java)) }
            Spacer(modifier = Modifier.height(100.dp)) // Extra space at the bottom
        }
    }

    @Composable
    fun SmallItemBox(text: String, onClick: () -> Unit) {
        Box(
            contentAlignment = Alignment.Center, // Center the text horizontally and vertically
            modifier = Modifier
                .clickable(onClick = onClick)
                .width(120.dp)
                .height(60.dp)
                .background(Color.Black, shape = RoundedCornerShape(16.dp))
                .border(2.dp, Color.Yellow, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
    }

    @Composable
    fun LargeItemBox(text: String, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .height(150.dp) // Use Dp type for height
                .background(Color.Black, shape = RoundedCornerShape(16.dp))
                .border(2.dp, Color.Yellow, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
    }

    @Composable
    fun BottomButtons(username: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color(0xFF121212)), // Ensure buttons are always visible
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* No action needed, already on Prehľad */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text("Prehľad", color = Color.Black)
            }
            Button(
                onClick = {
                    val intent = Intent(this@HomeActivity, KontaktyActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text("Kontakty", color = Color.Black)
            }
        }
    }
}
