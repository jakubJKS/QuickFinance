package com.example.loginsignupsql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_NAME = "data"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FULLNAME = "fullname"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_ADDRESS = "address"

        // New table and columns for contacts
        const val TABLE_CONTACTS = "contacts"
        const val COLUMN_CONTACT_ID = "id"
        const val COLUMN_CONTACT_FIRSTNAME = "firstname"
        const val COLUMN_CONTACT_LASTNAME = "lastname"
        const val COLUMN_CONTACT_IBAN = "iban"
        const val COLUMN_USER_ID = "user_id" // Foreign key reference to users
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

        val createContactsTableQuery = ("CREATE TABLE $TABLE_CONTACTS (" +
                "$COLUMN_CONTACT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_CONTACT_FIRSTNAME TEXT," +
                "$COLUMN_CONTACT_LASTNAME TEXT," +
                "$COLUMN_CONTACT_IBAN TEXT," +
                "$COLUMN_USER_ID INTEGER, " +
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_NAME($COLUMN_ID))")
        db?.execSQL(createContactsTableQuery)

        Log.d("DatabaseHelper", "Database created with query: $createTableQuery")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion...")
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        val dropContactsTableQuery = "DROP TABLE IF EXISTS $TABLE_CONTACTS"
        db?.execSQL(dropContactsTableQuery)
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

    // New methods for contacts
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
        // Using alias for _id
        return db.rawQuery("SELECT $COLUMN_CONTACT_ID AS _id, $COLUMN_CONTACT_FIRSTNAME, $COLUMN_CONTACT_LASTNAME, $COLUMN_CONTACT_IBAN FROM $TABLE_CONTACTS WHERE $COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }

    fun deleteContact(id: Long) {
        val db = writableDatabase
        val selection = "$COLUMN_CONTACT_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(TABLE_CONTACTS, selection, selectionArgs)
    }

    fun getUserId(username: String): Long {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_ID), selection, selectionArgs, null, null, null)
        return if (cursor.moveToFirst()) {
            val userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            cursor.close()
            userId
        } else {
            cursor.close()
            -1
        }
    }
}
