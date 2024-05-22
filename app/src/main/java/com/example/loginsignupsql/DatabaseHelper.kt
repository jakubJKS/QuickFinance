package com.example.loginsignupsql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_FULLNAME = "fullname"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_ADDRESS = "address"
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
                "$COLUMN_ADDRESS TEXT)")
        db?.execSQL(createTableQuery)
        Log.d("DatabaseHelper", "Database created with query: $createTableQuery")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion...")
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        Log.d("DatabaseHelper", "Database dropped with query: $dropTableQuery")
        onCreate(db)
    }

    fun insertUser(username: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
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
                // You can add other columns if needed
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
}
