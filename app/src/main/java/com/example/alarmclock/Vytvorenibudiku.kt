package com.example.alarmclock

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TimePicker
import android.widget.Switch

import androidx.appcompat.app.AppCompatActivity

class Vytvorenibudiku : AppCompatActivity() {

    lateinit var CancelButton: Button
    lateinit var SaveButton: Button
    lateinit var timePicker: TimePicker
    lateinit var dbHelper: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vyt_budiku)

        dbHelper = MyDBHelper(this)
        val db = dbHelper.writableDatabase

        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)

        CancelButton = findViewById(R.id.cancel_button)
        CancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        SaveButton = findViewById(R.id.save_button)
        SaveButton.setOnClickListener {
            saveAlarm()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveAlarm() {
        val time = "${timePicker.hour}:${timePicker.minute}"
        val monday = findViewById<CheckBox>(R.id.checkBox1).isChecked
        val tuesday = findViewById<CheckBox>(R.id.checkBox2).isChecked
        val wednesday = findViewById<CheckBox>(R.id.checkBox3).isChecked
        val thursday = findViewById<CheckBox>(R.id.checkBox4).isChecked
        val friday = findViewById<CheckBox>(R.id.checkBox5).isChecked
        val saturday = findViewById<CheckBox>(R.id.checkBox6).isChecked
        val sunday = findViewById<CheckBox>(R.id.checkBox7).isChecked
        val gentle = findViewById<Switch>(R.id.switch1).isChecked

        insertRecord(time, monday, tuesday, wednesday, thursday, friday, saturday, sunday, gentle)
    }

    private fun insertRecord(time: String, monday: Boolean, tuesday: Boolean, wednesday: Boolean, thursday: Boolean, friday: Boolean, saturday: Boolean, sunday: Boolean, gentle: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("time", time)
            put("monday", monday)
            put("tuesday", tuesday)
            put("wednesday", wednesday)
            put("thursday", thursday)
            put("friday", friday)
            put("saturday", saturday)
            put("sunday", sunday)
            put("gentle", gentle)
        }
        val newRowId = db.insert("MyTable", null, values)
        if (newRowId == -1L) {
            // Chyba při vkládání záznamu do databáze
            Log.e("InsertRecord", "Chyba při vkládání záznamu do databáze")
        } else {
            // Vložení záznamu proběhlo úspěšně
            Log.d("InsertRecord", "Záznam byl úspěšně vložen do databáze. ID nového záznamu: $newRowId")
        }
    }
}
