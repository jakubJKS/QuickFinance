package com.example.loginsignupsql

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.random.Random

class AddTransactionWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val db = DatabaseHelper(context)

    override fun doWork(): Result {
        val cursor = db.getAllUsers()
        with(cursor) {
            while (moveToNext()) {
                val userId = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val randomAmount = Random.nextDouble(10.0, 150.0)
                val user = db.getUserById(userId)
                if (user.createdAt <= db.getCurrentDateTime()) {
                    db.insertTransaction(randomAmount, userId)
                }
            }
            close()
        }
        return Result.success()
    }
}
