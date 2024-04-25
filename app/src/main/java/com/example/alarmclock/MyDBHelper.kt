package com.example.alarmclock

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "MyDatabase.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableQuery = "CREATE TABLE IF NOT EXISTS MyTable(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "time TEXT, " +
                "monday BOOLEAN, " +
                "tuesday BOOLEAN, " +
                "wednesday BOOLEAN, " +
                "thursday BOOLEAN, " +
                "friday BOOLEAN, " +
                "saturday BOOLEAN, " +
                "sunday BOOLEAN, " +
                "gentle BOOLEAN)"
        db?.execSQL(createTableQuery)

        val tableCreated = checkTableCreated(db)
        if (tableCreated) {
            Log.e("MyDBHelper", "Databáze úspěšně vytvořena")
        } else {
            Log.e("MyDBHelper", "Chyba při vytváření databáze")
        }
    }
    private fun checkTableCreated(db: SQLiteDatabase?): Boolean {
        return db != null
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS MyTable" )
        onCreate(db)
    }
}
