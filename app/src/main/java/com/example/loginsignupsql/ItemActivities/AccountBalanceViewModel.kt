package com.example.loginsignupsql.itemactivities

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.loginsignupsql.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class AccountBalanceViewModel(private val context: Context, private val userId: Long) : ViewModel() {
    private val db = DatabaseHelper(context)
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Load transactions for the current user
            loadTransactionsForUser(userId)
            startPeriodicTransactionUpdate()
        }
    }

    fun refreshTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            // Reload transactions for the current user
            loadTransactionsForUser(userId)
        }
    }

    private suspend fun startPeriodicTransactionUpdate() {
        while (true) {
            delay(10000) // Wait for 10 seconds
            addRandomTransactionForCurrentUser()
        }
    }

    private suspend fun addRandomTransactionForCurrentUser() {
        val randomAmount = Random.nextDouble(10.0, 150.0)
        db.insertTransaction(randomAmount, userId)
        loadTransactionsForUser(userId)
    }

    private suspend fun loadTransactionsForUser(userId: Long) {
        val cursor = db.getTransactions(userId) // Fetch transactions for the specified user ID
        val transactionsList = mutableListOf<Transaction>()
        with(cursor) {
            while (moveToNext()) {
                val amount = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRANSACTION_AMOUNT))
                val timestamp = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRANSACTION_TIMESTAMP))
                transactionsList.add(Transaction(amount, timestamp))
            }
            close()
        }
        _transactions.postValue(transactionsList)
    }

}

data class Transaction(val amount: Double, val timestamp: String)

class AccountBalanceViewModelFactory(private val context: Context, private val userId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountBalanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountBalanceViewModel(context, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
