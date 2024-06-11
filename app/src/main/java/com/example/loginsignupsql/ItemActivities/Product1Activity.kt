package com.example.loginsignupsql.ItemActivities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import com.example.loginsignupsql.DatabaseHelper
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme

class Product1Activity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                    PaymentOptions(dbHelper, userId)
                }
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color(0xFF121212).toArgb()

        dbHelper = DatabaseHelper(this)
        val username = intent.getStringExtra("username")
        Log.d("Product1Activity", "username: $username")
        if (username != null) {
            userId = dbHelper.getUserId(username)
            Log.d("Product1Activity", "UserId: $userId")
        } else {
            Toast.makeText(this, "Username not found, returning to previous screen.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

@Composable
fun PaymentOptions(dbHelper: DatabaseHelper, userId: Long) {
    var showManualPaymentDialog by rememberSaveable { mutableStateOf(false) }
    var showConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var recipientName by rememberSaveable { mutableStateOf("") }
    var recipientSurname by rememberSaveable { mutableStateOf("") }
    var recipientIban by rememberSaveable { mutableStateOf("") }
    var paymentAmount by rememberSaveable { mutableStateOf("") }
    var payments by remember { mutableStateOf(listOf<String>()) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        payments = dbHelper.getUserPayments(userId).use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor else null }
                .map {
                    "Amount: ${cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_AMOUNT))}"
                }
                .toList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Select Payment Option",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                showManualPaymentDialog = true
            },
            colors = ButtonDefaults.buttonColors()
        ) {
            Text("Manual Payment")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Selecting Recipient from Contacts", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(contentColor = Color.Black)
        ) {
            Text("Select Recipient from Contacts")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            payments.forEach { payment ->
                Text(text = payment, color = Color.White, modifier = Modifier.padding(4.dp))
            }
        }

        if (showManualPaymentDialog) {
            Dialog(
                onDismissRequest = { showManualPaymentDialog = false },
                properties = DialogProperties()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = Color(0xFF333333)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color(0xFF333333))
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Manual Payment",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "First Name", color = Color.White, fontSize = 16.sp)
                        BasicTextField(
                            value = recipientName,
                            onValueChange = { recipientName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray)
                                .padding(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Surname", color = Color.White, fontSize = 16.sp)
                        BasicTextField(
                            value = recipientSurname,
                            onValueChange = { recipientSurname = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray)
                                .padding(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "IBAN", color = Color.White, fontSize = 16.sp)
                        BasicTextField(
                            value = recipientIban,
                            onValueChange = { recipientIban = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray)
                                .padding(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Amount", color = Color.White, fontSize = 16.sp)
                        BasicTextField(
                            value = paymentAmount,
                            onValueChange = { paymentAmount = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray)
                                .padding(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (recipientName.isNotEmpty() && recipientSurname.isNotEmpty() && recipientIban.isNotEmpty() && paymentAmount.isNotEmpty()) {
                                        showManualPaymentDialog = false
                                        showConfirmationDialog = true
                                    } else {
                                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (recipientName.isNotEmpty() && recipientSurname.isNotEmpty() && recipientIban.isNotEmpty() && paymentAmount.isNotEmpty()) {
                                    showManualPaymentDialog = false
                                    showConfirmationDialog = true
                                } else {
                                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }

        if (showConfirmationDialog) {
            Dialog(
                onDismissRequest = { showConfirmationDialog = false },
                properties = DialogProperties()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = Color(0xFF333333)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color(0xFF333333))
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Confirm Payment",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Name: $recipientName $recipientSurname",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "IBAN: $recipientIban",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "Amount: $paymentAmount",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    try {
                                        val amount = paymentAmount.toDouble()
                                        payments = payments + "Amount: $amount"
                                        // Ensure the correct userId is passed here
                                        dbHelper.insertUserPayment(userId, recipientName, recipientSurname, recipientIban, amount)
                                        showConfirmationDialog = false
                                        Toast.makeText(context, "Payment Successful", Toast.LENGTH_SHORT).show()
                                        recipientName = ""
                                        recipientSurname = ""
                                        recipientIban = ""
                                        paymentAmount = ""
                                    } catch (e: NumberFormatException) {
                                        Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                                enabled = recipientName.isNotEmpty() && recipientSurname.isNotEmpty() && recipientIban.isNotEmpty() && paymentAmount.isNotEmpty()
                            ) {
                                Text("Confirm")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = {
                                    showConfirmationDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}
