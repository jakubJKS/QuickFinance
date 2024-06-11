package com.example.loginsignupsql.itemactivities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.loginsignupsql.AddTransactionWorker
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class AccountBalanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the status bar color
        window.statusBarColor = Color(0xFF121212).toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        val userId = getCurrentUserId()
        Log.d("AccountBalanceActivity", "Current User ID: $userId")

        // Schedule the periodic work
        val workRequest = PeriodicWorkRequestBuilder<AddTransactionWorker>(15, TimeUnit.MINUTES)
            .setInputData(workDataOf("user_id" to userId))
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AddTransactionWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                    AccountBalanceScreen(userId)
                }
            }
        }
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", 1L)
        Log.d("AccountBalanceActivity", "Retrieved User ID from SharedPreferences: $userId")
        return userId
    }
}

@Composable
fun AccountBalanceScreen(userId: Long) {
    val context = LocalContext.current
    val viewModel: AccountBalanceViewModel = viewModel(factory = AccountBalanceViewModelFactory(context, userId))
    val transactions by viewModel.transactions.observeAsState(emptyList())
    val totalAmount = transactions.sumByDouble { it.amount }
    val coroutineScope = rememberCoroutineScope()

    // Load transactions initially
    LaunchedEffect(Unit) {
        Log.d("AccountBalanceScreen", "Loading initial transactions for User ID: $userId")
        viewModel.refreshTransactions()
    }

    // Start periodic transaction refresh
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000) // Every 10 seconds
            Log.d("AccountBalanceScreen", "Refreshing transactions for User ID: $userId")
            viewModel.refreshTransactions()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Account Balance Activity",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Total Amount: €${"%.2f".format(totalAmount)}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Amount: €${"%.2f".format(transaction.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
        Text(
            text = transaction.timestamp, // Displaying the timestamp directly
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}
