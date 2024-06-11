package com.example.loginsignupsql

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.random.Random

class AddTransactionWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val db = DatabaseHelper(context)

    override fun doWork(): Result {
        val userId = inputData.getLong("user_id", -1)
        if (userId == -1L) {
            Log.e("AddTransactionWorker", "Invalid User ID: $userId")
            return Result.failure()
        }

        val randomAmount = Random.nextDouble(10.0, 150.0)
        db.insertTransaction(randomAmount, userId)
        Log.d("AddTransactionWorker", "Inserted random transaction of amount $randomAmount for User ID: $userId")

        return Result.success()
    }
}
