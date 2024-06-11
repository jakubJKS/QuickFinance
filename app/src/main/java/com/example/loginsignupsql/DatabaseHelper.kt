package com.example.loginsignupsql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 6
        const val TABLE_NAME = "data"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FULLNAME = "fullname"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_CREATED_AT = "created_at"

        const val TABLE_CONTACTS = "contacts"
        const val COLUMN_CONTACT_ID = "id"
        const val COLUMN_CONTACT_FIRSTNAME = "firstname"
        const val COLUMN_CONTACT_LASTNAME = "lastname"
        const val COLUMN_CONTACT_IBAN = "iban"
        const val COLUMN_USER_ID = "user_id"

        const val TABLE_TRANSACTIONS = "transactions"
        const val COLUMN_TRANSACTION_ID = "id"
        const val COLUMN_TRANSACTION_AMOUNT = "amount"
        const val COLUMN_TRANSACTION_TIMESTAMP = "timestamp"
        const val COLUMN_TRANSACTION_CREATED_AT = "created_at"
        const val COLUMN_TRANSACTION_USER_ID = "user_id"

        // New table for user-specific payments in Product1Activity
        const val TABLE_USER_PAYMENTS = "user_payments"
        const val COLUMN_PAYMENT_ID = "payment_id"
        const val COLUMN_PAYMENT_USER_ID = "user_id"
        const val COLUMN_PAYMENT_RECIPIENT_NAME = "recipient_name"
        const val COLUMN_PAYMENT_RECIPIENT_SURNAME = "recipient_surname"
        const val COLUMN_PAYMENT_IBAN = "iban"
        const val COLUMN_PAYMENT_AMOUNT = "amount"
        const val COLUMN_PAYMENT_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("DatabaseHelper", "Creating database...")
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT UNIQUE," +
                "$COLUMN_PASSWORD TEXT," +
                "$COLUMN_FULLNAME TEXT," +
                "$COLUMN_EMAIL TEXT," +
                "$COLUMN_PHONE TEXT," +
                "$COLUMN_ADDRESS TEXT," +
                "$COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP)")
        db?.execSQL(createTableQuery)

        val createContactsTableQuery = ("CREATE TABLE $TABLE_CONTACTS (" +
                "$COLUMN_CONTACT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_CONTACT_FIRSTNAME TEXT," +
                "$COLUMN_CONTACT_LASTNAME TEXT," +
                "$COLUMN_CONTACT_IBAN TEXT," +
                "$COLUMN_USER_ID INTEGER, " +
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_NAME($COLUMN_ID))")
        db?.execSQL(createContactsTableQuery)

        val createTransactionsTableQuery = ("CREATE TABLE $TABLE_TRANSACTIONS (" +
                "$COLUMN_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TRANSACTION_AMOUNT REAL," +
                "$COLUMN_TRANSACTION_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "$COLUMN_TRANSACTION_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "$COLUMN_TRANSACTION_USER_ID INTEGER, " +
                "FOREIGN KEY($COLUMN_TRANSACTION_USER_ID) REFERENCES $TABLE_NAME($COLUMN_ID))")
        db?.execSQL(createTransactionsTableQuery)

        // Create user_payments table
        val createUserPaymentsTableQuery = ("CREATE TABLE $TABLE_USER_PAYMENTS (" +
                "$COLUMN_PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PAYMENT_USER_ID INTEGER," +
                "$COLUMN_PAYMENT_RECIPIENT_NAME TEXT," +
                "$COLUMN_PAYMENT_RECIPIENT_SURNAME TEXT," +
                "$COLUMN_PAYMENT_IBAN TEXT," +
                "$COLUMN_PAYMENT_AMOUNT REAL," +
                "$COLUMN_PAYMENT_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY($COLUMN_PAYMENT_USER_ID) REFERENCES $TABLE_NAME($COLUMN_ID))")
        db?.execSQL(createUserPaymentsTableQuery)

        Log.d("DatabaseHelper", "Database created with query: $createTableQuery")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion...")
        if (oldVersion < 5) {
            if (!isColumnExists(db, TABLE_NAME, COLUMN_CREATED_AT)) {
                db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_CREATED_AT DATETIME")
                val currentTime = getCurrentDateTime()
                db?.execSQL("UPDATE $TABLE_NAME SET $COLUMN_CREATED_AT = '$currentTime' WHERE $COLUMN_CREATED_AT IS NULL")
            }

            val createTransactionsTableQuery = ("CREATE TABLE IF NOT EXISTS $TABLE_TRANSACTIONS (" +
                    "$COLUMN_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_TRANSACTION_AMOUNT REAL," +
                    "$COLUMN_TRANSACTION_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "$COLUMN_TRANSACTION_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "$COLUMN_TRANSACTION_USER_ID INTEGER, " +
                    "FOREIGN KEY($COLUMN_TRANSACTION_USER_ID) REFERENCES $TABLE_NAME($COLUMN_ID))")
            db?.execSQL(createTransactionsTableQuery)
        }

        if (oldVersion < 6) {
            val createUserPaymentsTableQuery = ("CREATE TABLE IF NOT EXISTS $TABLE_USER_PAYMENTS (" +
                    "$COLUMN_PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_PAYMENT_USER_ID INTEGER," +
                    "$COLUMN_PAYMENT_RECIPIENT_NAME TEXT," +
                    "$COLUMN_PAYMENT_RECIPIENT_SURNAME TEXT," +
                    "$COLUMN_PAYMENT_IBAN TEXT," +
                    "$COLUMN_PAYMENT_AMOUNT REAL," +
                    "$COLUMN_PAYMENT_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY($COLUMN_PAYMENT_USER_ID) REFERENCES $TABLE_NAME($COLUMN_ID))")
            db?.execSQL(createUserPaymentsTableQuery)
        }
    }

    private fun isColumnExists(db: SQLiteDatabase?, tableName: String, columnName: String): Boolean {
        val cursor = db?.rawQuery("PRAGMA table_info($tableName)", null)
        cursor?.use {
            val index = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (cursor.getString(index) == columnName) {
                    return true
                }
            }
        }
        return false
    }

    fun insertUser(username: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_CREATED_AT, getCurrentDateTime())
        }
        val result = db.insert(TABLE_NAME, null, values)
        Log.d("DatabaseHelper", "insertUser: Inserted user with username = $username, result = $result")
        return result
    }

    fun readUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        val userExists = cursor.count > 0
        cursor.close()
        Log.d("DatabaseHelper", "readUser: username = $username, userExists = $userExists")
        return userExists
    }

    fun isUserExists(username: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        val userExists = cursor.count > 0
        cursor.close()
        Log.d("DatabaseHelper", "isUserExists: username = $username, userExists = $userExists")
        return userExists
    }

    fun updateUserInformation(username: String, fullname: String, email: String, phone: String, address: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FULLNAME, fullname)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_ADDRESS, address)
        }
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        try {
            val updatedRows = db.update(TABLE_NAME, values, selection, selectionArgs)
            Log.d("DatabaseHelper", "updateUserInformation: username = $username, updatedRows = $updatedRows")
            if (updatedRows == 0) {
                throw Exception("No rows were updated, possibly user not found")
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error updating user information", e)
            throw e
        }
    }

    fun getUserInformation(username: String): ContentValues? {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        if (cursor.moveToFirst()) {
            val values = ContentValues().apply {
                put(COLUMN_USERNAME, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)))
                put(COLUMN_FULLNAME, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME)))
                put(COLUMN_EMAIL, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)))
                put(COLUMN_PHONE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)))
                put(COLUMN_ADDRESS, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)))
            }
            cursor.close()
            Log.d("DatabaseHelper", "Fetched User Information: $values")
            return values
        } else {
            cursor.close()
            Log.e("DatabaseHelper", "No user information found for username: $username")
            return null
        }
    }

    fun insertContact(firstname: String, lastname: String, iban: String, userId: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTACT_FIRSTNAME, firstname)
            put(COLUMN_CONTACT_LASTNAME, lastname)
            put(COLUMN_CONTACT_IBAN, iban)
            put(COLUMN_USER_ID, userId)
        }
        return db.insert(TABLE_CONTACTS, null, values)
    }

    fun getAllContacts(userId: Long): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT $COLUMN_CONTACT_ID AS _id, $COLUMN_CONTACT_FIRSTNAME, $COLUMN_CONTACT_LASTNAME, $COLUMN_CONTACT_IBAN FROM $TABLE_CONTACTS WHERE $COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }

    fun deleteContact(id: Long) {
        val db = writableDatabase
        val selection = "$COLUMN_CONTACT_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(TABLE_CONTACTS, selection, selectionArgs)
    }

    fun getUserId(username: String): Long {
        Log.d("DatabaseHelper", "getUserId: username = $username")
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_ID), selection, selectionArgs, null, null, null)
        return if (cursor.moveToFirst()) {
            val userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            cursor.close()
            Log.d("DatabaseHelper", "Found userId for username $username: $userId")
            userId
        } else {
            cursor.close()
            Log.d("DatabaseHelper", "No userId found for username: $username")
            -1
        }
    }



    fun insertTransaction(amount: Double, userId: Long) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TRANSACTION_AMOUNT, amount)
            put(COLUMN_TRANSACTION_TIMESTAMP, getCurrentDateTime())
            put(COLUMN_TRANSACTION_CREATED_AT, getCurrentDateTime())
            put(COLUMN_TRANSACTION_USER_ID, userId)
        }
        db.insert(TABLE_TRANSACTIONS, null, values)
    }

    fun getTransactions(userId: Long): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_TRANSACTIONS,
            null,
            "$COLUMN_TRANSACTION_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_TRANSACTION_TIMESTAMP DESC"
        )
    }

    fun getAllUsers(): Cursor {
        val db = readableDatabase
        return db.query(TABLE_NAME, arrayOf(COLUMN_ID, COLUMN_CREATED_AT), null, null, null, null, null)
    }

    fun getUserById(userId: Long): User {
        val db = readableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(userId.toString())
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            throw IllegalArgumentException("User not found")
        }
    }

    fun insertUserPayment(userId: Long, recipientName: String, recipientSurname: String, iban: String, amount: Double) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PAYMENT_USER_ID, userId)  // Ensure user_id is correctly set
            put(COLUMN_PAYMENT_RECIPIENT_NAME, recipientName)
            put(COLUMN_PAYMENT_RECIPIENT_SURNAME, recipientSurname)
            put(COLUMN_PAYMENT_IBAN, iban)
            put(COLUMN_PAYMENT_AMOUNT, amount)
            put(COLUMN_PAYMENT_TIMESTAMP, getCurrentDateTime())
        }
        Log.d("DatabaseHelper", "Inserting payment: userId=$userId, recipientName=$recipientName, recipientSurname=$recipientSurname, iban=$iban, amount=$amount")
        db.insert(TABLE_USER_PAYMENTS, null, values)
    }


    fun getUserPayments(userId: Long): Cursor {
        val db = readableDatabase
        Log.d("DatabaseHelper", "Fetching payments for userId=$userId")
        return db.query(
            TABLE_USER_PAYMENTS,
            null,
            "$COLUMN_PAYMENT_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_PAYMENT_TIMESTAMP DESC"
        )
    }






    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}

data class User(val id: Long, val username: String, val createdAt: String)